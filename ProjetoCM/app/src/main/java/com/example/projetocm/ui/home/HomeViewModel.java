package com.example.projetocm.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.Quiz;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Quiz>> quizes;
    private MutableLiveData<Quiz> quiz;

    public LiveData<List<Quiz>> getQuizes() {
        if(quizes ==null){
            quizes = new MutableLiveData<>();
        }
        return quizes;
    }

    public LiveData<Quiz> getQuiz(){
        if(quiz == null){
            quiz = new MutableLiveData<>();
        }
        return quiz;
    }

    public void setQuizes(List<Quiz> todos){
        quizes.setValue(todos);
    }

    public void setQuiz(Quiz qui) {
        if(quiz == null){
            quiz = new MutableLiveData<>();
        }
        quiz.setValue(qui);
    }
}