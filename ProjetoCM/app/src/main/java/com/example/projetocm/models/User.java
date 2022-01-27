package com.example.projetocm.models;

import com.google.api.ResourceDescriptor;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String name;
    private String biography;
    private String email;
    private String photoRef;
    private String userId;

    private int totalScore;

    // Needed for firestore database
    public User() {
    }

    public User(String userId, String name, String email, String biography, String photoRef) {
        this.userId = userId;
        this.name = name;
        this.biography = biography;
        this.photoRef = photoRef;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
