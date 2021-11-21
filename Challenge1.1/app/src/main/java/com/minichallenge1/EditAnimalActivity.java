package com.minichallenge1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EditAnimalActivity extends AppCompatActivity {
    public static final String EXTRA_TEXT = "com.minichallenge1.EXTRA_TEXT";
    public static final String EXTRA_NUMBER = "com.minichallenge.EXTRA_NUMBER";
    Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);
        setTitle("Edit Animal Data");

        Intent intent = getIntent();
        animal = (Animal) intent.getSerializableExtra("Animal");
        Button button = (Button) findViewById(R.id.save_button );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });
    }

    public void openMainActivity() {
        EditText editTextOwner = (EditText) findViewById(R.id.edit_animal_owner);
        String owner = editTextOwner.getText().toString();
        EditText editTextName = (EditText) findViewById(R.id.edit_animal_name);
        String name = editTextName.getText().toString();
        EditText editTextAge = (EditText) findViewById(R.id.edit_animal_age);
        String ageStr = editTextAge.getText().toString();

        if (owner.isEmpty() && name.isEmpty() && ageStr.isEmpty()) {
            String noChange = "No changes made to " + animal.getSpecie() + "!";
            Toast.makeText(this, noChange, Toast.LENGTH_LONG).show();
        }
        else {
            if (!(owner.isEmpty())) {
                animal.setOwner(owner);
            }
            if (!(name.isEmpty())) {
                animal.setName(name);
            }
            if (!(ageStr.isEmpty())) {
                int age = Integer.parseInt(ageStr);
                animal.setAge(age);
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("Animal", animal);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}