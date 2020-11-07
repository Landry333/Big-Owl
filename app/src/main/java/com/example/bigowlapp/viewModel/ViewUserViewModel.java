package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.ViewUserRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ViewUserViewModel extends ViewModel {

    private ViewUserRepository viewUserRepository;

    public ViewUserViewModel() {
        viewUserRepository = new ViewUserRepository();
    }

    public FirebaseUser getCurrentUser() {
        return viewUserRepository.getCurrentUser();
    }

    public DatabaseReference getCurrentRequestRef() {
        return viewUserRepository.getCurrentRequestRef();
    }
}
