package com.minichallenge1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private ImageView imageView;
    private String itemName;
    private Button button;
    private Animal animal;
    List<Animal> animalList = new ArrayList<>();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imganimal);
        spinner = findViewById(R.id.spinner);
        textView = (TextView) findViewById(R.id.animal_info);

        Animal frog = new Animal("Frog", "João", "Kermit", 2);
        animalList.add(frog);
        Animal rhino = new Animal("Rhino", "Cesário", "Simão", 10);
        animalList.add(rhino);
        Animal snail = new Animal("Snail", "Alguem", "Júlio", 1);
        animalList.add(snail);

        ArrayAdapter<Animal> adapter = new ArrayAdapter<Animal>(this, android.R.layout.simple_spinner_dropdown_item, animalList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                animal = (Animal) adapterView.getSelectedItem();
                displayAnimalData(animal);
                changeAnimalImg();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditAnimalActivity();
            }
        });
    }

    public void getSelectedAnimal(View v) {
        Animal animal = (Animal) spinner.getSelectedItem();
        displayAnimalData(animal);
    }

    private void displayAnimalData(Animal animal) {
        String owner = animal.getOwner();
        String name = animal.getName();
        int age = animal.getAge();

        String animalData = "Owner: " + owner + "\nName: " + name + "\nAge: " + age;
        textView.setText(animalData);
    }

    private void changeAnimalImg() {
        itemName = spinner.getSelectedItem().toString();
        switch (itemName) {
            case "Frog":
                imageView.setImageResource(R.drawable.frog);
                break;
            case "Rhino":
                imageView.setImageResource(R.drawable.rhino);
                break;
            case "Snail":
                imageView.setImageResource(R.drawable.snail);
                break;
        }
    }

    public void openEditAnimalActivity() {
        Intent intent = new Intent(this, EditAnimalActivity.class);
        intent.putExtra("Animal", animal);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Animal result = (Animal) data.getSerializableExtra("Animal");
                for (int i = 0; i < animalList.size(); i++) {
                    if (result.getSpecie().equals(animalList.get(i).getSpecie())) {
                        animalList.get(i).setOwner(result.getOwner());
                        animalList.get(i).setName(result.getName());
                        animalList.get(i).setAge(result.getAge());
                    }
                }
                String animalData = "Changes made to " + result.getSpecie() + "!";
                Toast.makeText(this, animalData, Toast.LENGTH_LONG).show();
                String animalUpdates = "Owner: " + result.getOwner() + "\nName: " + result.getName() + "\nAge: " + result.getAge();
                textView.setText(animalUpdates);
            }
        }
    }
}