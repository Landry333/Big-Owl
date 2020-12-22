package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.User;

public class HomePageViewModel extends BaseViewModel {

    private MutableLiveData<User> user;

    public HomePageViewModel() {
    }

    public LiveData<User> getCurrentUserData() {
        if (user == null) {
            user = new MutableLiveData<>();
            loadUserCurrentProfile();
        }
        return user;
    }

    private void loadUserCurrentProfile() {
        user = repositoryFacade.getUserRepository()
                .getDocumentByUid(getCurrentUserUid(), User.class);
    }
}
