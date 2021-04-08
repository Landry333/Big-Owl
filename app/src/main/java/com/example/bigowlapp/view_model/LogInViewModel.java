package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInViewModel extends BaseViewModel {

    private LiveDataWithStatus<User> user;

    public LogInViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public Task<AuthResult> logInUser(String email, String password) {
        return repositoryFacade.getAuthRepository().signInUser(email, password);
    }

    public LiveDataWithStatus<User> getCurrentUserData() {
        if (user == null) {
            user = new LiveDataWithStatus<>();
            loadUserCurrentProfile();
        }
        return user;
    }

    public void addAuthStateListenerToDatabase(FirebaseAuth.AuthStateListener authStateListener) {
        repositoryFacade.getAuthRepository().addAuthStateListener(authStateListener);
    }

    private void loadUserCurrentProfile() {
        user = repositoryFacade.getUserRepository()
                .getDocumentByUid(getCurrentUserUid(), User.class);
    }

}
