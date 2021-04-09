package com.example.bigowlapp.view_model;

import androidx.lifecycle.LiveData;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;

public class SearchContactsToSuperviseViewModel extends BaseViewModel {

    private LiveDataWithStatus<User> userToAddData;

    public LiveData<User> getUserToAdd(String number) {
        userToAddData = repositoryFacade.getUserRepository()
                .getDocumentByAttribute(User.Field.PHONE_NUMBER, number, User.class);
        return userToAddData;
    }
}
