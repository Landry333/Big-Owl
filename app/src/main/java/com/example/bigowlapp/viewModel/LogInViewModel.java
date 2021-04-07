package com.example.bigowlapp.viewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInViewModel extends BaseViewModel {

    public LogInViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public Task<AuthResult> logInUser(String email, String password) {
        return repositoryFacade.getAuthRepository().signInUser(email, password);
    }

    public void addAuthStateListenerToDatabase(FirebaseAuth.AuthStateListener authStateListener) {
        repositoryFacade.getAuthRepository().addAuthStateListener(authStateListener);
    }

}
