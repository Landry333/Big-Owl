package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.User;

import androidx.lifecycle.LiveData;

public class SupervisedGroupPageViewModel extends BaseViewModel {
    public LiveData<User> getSupervisor(String supervisorUid) {
        return repositoryFacade.getUserRepository().getDocumentByUid(supervisorUid, User.class);
    }
}
