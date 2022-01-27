package com.example.projetocm.ui.recent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.History;

import java.util.List;

public class RecentActivityViewModel extends ViewModel {
    private MutableLiveData<List<History>> historyList;

    public LiveData<List<History>> getHistoryList() {
        if (historyList == null)
            historyList = new MutableLiveData<>();
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        if (this.historyList == null)
            this.historyList = new MutableLiveData<>();
        this.historyList.setValue(historyList);
    }
}
