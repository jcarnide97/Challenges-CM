package com.example.projetocm.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.History;
import com.example.projetocm.models.User;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> currentUser;
    private MutableLiveData<ArrayList<History>> history;



    public ProfileViewModel() {
        currentUser = new MutableLiveData<User>();
        history = new MutableLiveData<ArrayList<History>>();
    }

    public LiveData<User> getUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }



    public LiveData<ArrayList<History>> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<History> history) {
        this.history.setValue(history);
    }
}