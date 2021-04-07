package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;

import com.example.bigowlapp.model.User;

public class SupervisedGroupPageViewModel extends BaseViewModel {
    public LiveData<User> getSupervisor(String supervisorUid) {
        return repositoryFacade.getUserRepository().getDocumentByUid(supervisorUid, User.class);
    }
}
