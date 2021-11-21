package com.example.myapplication;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Animal implements Serializable {
    private String specie;
    private String owner;
    private String name;
    private int age;

    public Animal(String specie, String owner, String name, int age) {
        this.specie = specie;
        this.owner = owner;
        this.name = name;
        this.age = age;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return specie;
    }
}
