package com.example.bigowlapp.viewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LogInViewModel extends BaseViewModel {

    // TODO: Dependency Injection
    public LogInViewModel() {
    }

    public Task<Boolean> logInUser(String email, String password) {
        return repositoryFacade.getAuthRepository().signInUser(email, password);
    }

    public void addAuthStateListenerToDatabase(FirebaseAuth.AuthStateListener authStateListener) {
        repositoryFacade.getAuthRepository().addAuthStateListener(authStateListener);
    }

}
