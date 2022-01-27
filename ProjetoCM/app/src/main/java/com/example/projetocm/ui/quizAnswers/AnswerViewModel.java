package com.example.projetocm.ui.quizAnswers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.projetocm.ApiRepository;
import com.example.projetocm.models.Question;

import java.io.Serializable;

public class AnswerViewModel extends ViewModel implements Serializable {

    private MutableLiveData<Question> currentQuestion;

    private int counter;
    private ApiRepository apiRepository;

    private int currentScore;
    private int currentTime;
    private String quizId;
    private int currentCorrect;

    public AnswerViewModel() {
        this.counter=0;
    }

    public LiveData<Question> getCurrentQuestion() {
        if(currentQuestion == null){
            currentQuestion = new MutableLiveData<>();
        }
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion.setValue(currentQuestion);
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public int getCounter(){
        return counter;
    }

    public void increaseCounter() {
        counter++;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int increaseScore(int score) {
        return currentScore += score;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int increaseTime(int seconds) {
        return currentTime += seconds;
    }

    public int getCurrentCorrect() {
        return currentCorrect;
    }

    public int increaseCorrect() {
        return currentCorrect++;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public void setCurrentCorrect(int currentCorrect) {
        this.currentCorrect = currentCorrect;
    }

    public ApiRepository getApiRepository() {
        return apiRepository;
    }

    public void setApiRepository(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }
}
