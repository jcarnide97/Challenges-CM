package com.example.projetocm.ui.social;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.History;
import com.example.projetocm.models.User;

import java.util.List;

public class SocialViewModel extends ViewModel {

    private MutableLiveData<List<User>> users;
    private MutableLiveData<List<History>> histories;


    public SocialViewModel() {
        users = new MutableLiveData<>();
        histories = new MutableLiveData<>();
    }

    public LiveData<List<User>> getUsers() {
        if(users == null){
            users = new MutableLiveData<>();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users.setValue(users);
    }

    public LiveData<List<History>> getHistories() {
        if (histories == null) {
            histories = new MutableLiveData<>();
        }
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories.setValue(histories);
    }
}
