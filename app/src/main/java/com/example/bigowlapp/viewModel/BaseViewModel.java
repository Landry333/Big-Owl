package com.example.bigowlapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.repository.RepositoryFacade;

public abstract class BaseViewModel extends ViewModel {

    protected RepositoryFacade repositoryFacade;

    protected BaseViewModel() {
        repositoryFacade = RepositoryFacade.getInstance();
    }

    public String getCurrentUserUid() {
        return repositoryFacade.getCurrentUserUid();
    }

    public boolean isCurrentUserSet() {
        return repositoryFacade.getAuthRepository().getCurrentUser() != null;
    }
}
