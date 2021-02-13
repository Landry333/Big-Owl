package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseViewModel extends ViewModel {

    protected RepositoryFacade repositoryFacade;

    protected BaseViewModel() {
        repositoryFacade = RepositoryFacade.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return repositoryFacade.getAuthRepository().getCurrentUser();
    }

    public String getCurrentUserUid() {
        return repositoryFacade.getAuthRepository().getCurrentUser().getUid();
    }

    public boolean isCurrentUserSet() {
        return repositoryFacade.getAuthRepository().getCurrentUser() != null;
    }
}
