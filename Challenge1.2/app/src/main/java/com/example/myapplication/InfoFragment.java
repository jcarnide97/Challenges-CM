package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {
    private Spinner spinner;
    private ImageView imageView;
    private String itemName;
    private Button button;
    private Animal animal;
    List<Animal> animalList = new ArrayList<>();
    private TextView textView;
    private SharedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        getActivity().setTitle("We The Fragmented Animals");

        spinner = view.findViewById(R.id.spinner);
        imageView = view.findViewById(R.id.imganimal);
        textView = view.findViewById(R.id.animal_info);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (viewModel.getAnimal().getValue() == null) {
            viewModel.setAnimalList((ArrayList<Animal>) animalList);
        } else {
            animal = viewModel.getAnimal().getValue();
            viewModel.setAnimalList((ArrayList<Animal>) animalList);
            viewModel.updateAnimalList((ArrayList<Animal>) animalList, animal);
        }

        ArrayAdapter<Animal> adapter = new ArrayAdapter<Animal>(getContext(), android.R.layout.simple_spinner_dropdown_item, animalList);
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setAnimalData(animal);
                openEditAnimalFragment();
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

    public void openEditAnimalFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        EditFragment editFragment = new EditFragment();

        transaction.replace(R.id.info_fragment, editFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}