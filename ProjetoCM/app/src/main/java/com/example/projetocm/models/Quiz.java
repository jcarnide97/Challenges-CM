package com.example.projetocm.models;

import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String uid;
    private String name;
    private ApiCategory category;
    private String imageRef;
    private int numQuestions;
    private ApiDifficulty difficulty;
    private String createdBy;

    public enum ApiCategory {
        ALL("all"),
        BASH("bash"),
        DEVOPS("devops"),
        DOCKER("docker"),
        HTML("html"),
        JAVASCRIPT("javascript"),
        KUBERNETES("kubernetes"),
        LARAVEL("laravel"),
        LINUX("linux"),
        MYSQL("mysql"),
        PHP("php"),
        WORDPRESS("wordpress");

        private String text;

        ApiCategory(String s){
            this.text=s;
        }
        
        @Override
        public String toString() {
            return text;
        }
    }

    public enum ApiDifficulty {
        EASY("Easy"),
        MEDIUM("Medium"),
        HARD("Hard");

        private String text;

        ApiDifficulty(String s) {
            this.text=s;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public Quiz(){

    }

    public Quiz(String name, ApiCategory category, String imageRef, int numQuestions, ApiDifficulty difficulty, String createdBy) {
        this.name = name;
        this.category = category;
        this.imageRef = imageRef;
        this.numQuestions = numQuestions;
        this.difficulty = difficulty;
        this.createdBy = createdBy;
    }

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public ApiDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(ApiDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiCategory getCategory() {
        return category;
    }

    public void setCategory(ApiCategory category) {
        this.category = category;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
}
