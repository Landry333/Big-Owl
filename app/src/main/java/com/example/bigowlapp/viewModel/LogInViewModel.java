package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInViewModel extends ViewModel {

    private AuthRepository authRepository;

    // TODO: Dependency Injection
    public LogInViewModel() {
        authRepository = new AuthRepository();
    }

    public Task<Boolean> logInUser(String email, String password) {
        return authRepository.signInUser(email, password);
    }

    public void addAuthStateListenerToDatabase(FirebaseAuth.AuthStateListener authStateListener){
        authRepository.addAuthStateListener(authStateListener);
    }

    public FirebaseUser getCurrentUser(){
        return authRepository.getCurrentUser();
    }
}
