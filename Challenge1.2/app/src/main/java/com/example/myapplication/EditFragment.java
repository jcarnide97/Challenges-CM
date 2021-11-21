package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EditFragment extends Fragment {
    Animal animal;
    private SharedViewModel viewModel;
    private EditText editTextOwner;
    private EditText editTextName;
    private EditText editTextAge;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        animal = viewModel.getAnimal().getValue();

        editTextOwner = (EditText) view.findViewById(R.id.edit_animal_owner);
        editTextName = (EditText) view.findViewById(R.id.edit_animal_name);
        editTextAge = (EditText) view.findViewById(R.id.edit_animal_age);

        button = view.findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String owner = editTextOwner.getText().toString();
                String name = editTextName.getText().toString();
                String ageStr = editTextAge.getText().toString();

                if (owner.isEmpty()) {
                    animal.setOwner("");
                } else {
                    animal.setOwner(owner);
                }
                if (name.isEmpty()) {
                    animal.setName("");
                } else {
                    animal.setName(name);
                }
                if (ageStr.isEmpty()) {
                    animal.setAge(0);
                } else {
                    int age = Integer.parseInt(ageStr);
                    animal.setAge(age);
                }
                viewModel.setAnimalData(animal);

                String changes = "Changes made to " + animal.getSpecie() + "!";
                Toast.makeText(getActivity(), changes, Toast.LENGTH_SHORT).show();
                openInfoAnimalFragment();
            }
        });
    }

    public void openInfoAnimalFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        InfoFragment infoFragment = new InfoFragment();

        transaction.replace(R.id.info_fragment, infoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}