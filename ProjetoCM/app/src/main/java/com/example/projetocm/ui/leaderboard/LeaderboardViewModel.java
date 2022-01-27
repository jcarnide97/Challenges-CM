package com.example.projetocm.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.History;
import com.example.projetocm.models.User;

import java.util.List;

public class LeaderboardViewModel extends ViewModel {

    private MutableLiveData<List<User>> userList;

    private MutableLiveData<List<History>> historyList;

    public LiveData<List<User>> getUserList() {
        if (userList == null)
            userList = new MutableLiveData<>();
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList.setValue(userList);
    }

    public LiveData<List<History>> getHistoryList() {
        if (historyList == null) {
            historyList = new MutableLiveData<>();
        }
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList.setValue(historyList);
    }
}
