package com.example.myapplication;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Animal> animalData = new MutableLiveData<Animal>();
    private MutableLiveData<ArrayList<Animal>> animalList = new MutableLiveData<>();
    private ArrayList<Animal> newAnimalList = new ArrayList<>();

    public void setAnimalData(Animal animal) {
        animalData.setValue(animal);
    }

    public LiveData<Animal> getAnimal() {
        return animalData;
    }

    public void updateAnimalList(ArrayList<Animal> list, Animal newAnimal) {
        newAnimalList.add(newAnimal);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < newAnimalList.size(); j++) {
                if (newAnimalList.get(j).getSpecie().equals(list.get(i).getSpecie())) {
                    list.get(i).setOwner(newAnimalList.get(j).getOwner());
                    list.get(i).setName(newAnimalList.get(j).getName());
                    list.get(i).setAge(newAnimalList.get(j).getAge());
                }
            }
            /*
            if (newAnimal.getSpecie().equals(list.get(i).getSpecie())) {
                list.get(i).setOwner(newAnimal.getOwner());
                list.get(i).setName(newAnimal.getName());
                list.get(i).setAge(newAnimal.getAge());
            }
            */
        }
    }

    public void setAnimalList(ArrayList<Animal> list) {
        Animal frog = new Animal("Frog", "João", "Kermit", 2);
        list.add(frog);
        Animal rhino = new Animal("Rhino", "Cesário", "Simão", 10);
        list.add(rhino);
        Animal snail = new Animal("Snail", "Alguem", "Júlio", 1);
        list.add(snail);
        animalList.setValue(list);
    }

    public LiveData<ArrayList<Animal>> getAnimalList() {
        return animalList;
    }
}
