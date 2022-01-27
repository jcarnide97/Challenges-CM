package com.example.projetocm.models;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable {
    private Date takenOn;
    private int score;
    private int timeTaken;
    private int numCorrectQuestions;
    private String userId;
    private String userName;
    private String quizName;
    private String quizId;

    public History(){}

    public History(Date takenOn, int score, int timeTaken, int numCorrectQuestions) {
        this.takenOn = takenOn;
        this.score = score;
        this.timeTaken = timeTaken;
        this.numCorrectQuestions = numCorrectQuestions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public Date getTakenOn() {
        return takenOn;
    }

    public void setTakenOn(Date takenOn) {
        this.takenOn = takenOn;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getNumCorrectQuestions() {
        return numCorrectQuestions;
    }

    public void setNumCorrectQuestions(int numCorrectQuestions) {
        this.numCorrectQuestions = numCorrectQuestions;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    @Override
    public String toString() {
        return String.format("%s just took the %s and scored $d points", userName, quizName, score);
    }
}
