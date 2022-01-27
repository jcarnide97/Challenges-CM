package com.example.projetocm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projetocm.models.User;

public class HomepageActivityViewModel extends ViewModel {

    private MutableLiveData<User> currentUser;
    private Repository repository;
    private ApiRepository apiRepository;

    public HomepageActivityViewModel() {
        currentUser = new MutableLiveData<>();
    }

    public LiveData<User> getCurrentUser() {
        if (currentUser == null) {
            currentUser = new MutableLiveData<>();
        }
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    public Repository getRepository() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ApiRepository getApiRepository() {
        if (apiRepository == null) {
            apiRepository = new ApiRepository();
        }
        return apiRepository;
    }

    public void setApiRepository(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }
}
