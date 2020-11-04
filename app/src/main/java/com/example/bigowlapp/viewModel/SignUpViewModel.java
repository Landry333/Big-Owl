package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.AuthRepository;
import com.google.android.gms.tasks.Task;

public class SignUpViewModel extends ViewModel {

    private AuthRepository authRepository;

    // TODO: Dependency Injection
    public SignUpViewModel() {
        authRepository = new AuthRepository();
    }

    // TODO: Create a default group upon user creation
    public Task<Boolean> createUser(String email, String password, String phoneNumber, String name) {
        return authRepository.signUpUser(email, password, phoneNumber, nameExtractor(name)[0],
                nameExtractor(name)[1]);
    }

    public String nameParcer(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String[] nameExtractor(String name) {
        return name.split(" ", 2);
    }

}
