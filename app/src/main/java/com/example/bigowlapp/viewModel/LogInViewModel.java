package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.AuthRepository;
import com.google.android.gms.tasks.Task;

public class LogInViewModel extends ViewModel {

    private AuthRepository authRepository;

    // TODO: Dependency Injection
    public LogInViewModel() {
        authRepository = new AuthRepository();
    }

    public Task<Boolean> logInUser(String email, String password) {
        return authRepository.signInUser(email, password);
    }
}
