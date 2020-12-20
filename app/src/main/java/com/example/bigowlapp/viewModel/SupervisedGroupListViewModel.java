package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.UserRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupervisedGroupListViewModel extends BaseViewModel {
    private MutableLiveData<List<Group>> groupLiveData;
    private MutableLiveData<User> userData;

    public SupervisedGroupListViewModel() {
    }

    public LiveData<List<Group>> getSupervisedGroupListData() {
        if (groupLiveData == null) {
            loadListOfDocumentByArrayContains();
        }
        return groupLiveData;
    }

    public LiveData<User> getSupervisor(String supervisorUid) {
        return repositoryFacade.getUserRepository().getDocumentByUId(supervisorUid, User.class);
    }

    private void loadListOfDocumentByArrayContains() {
        groupLiveData = repositoryFacade.getGroupRepository()
                .getListOfDocumentByArrayContains("supervisedUserId",
                        getCurrentUserUid(), Group.class);
    }

    public LiveData<User> getCurrentUserData() {
        if (userData == null) {
            userData = new MutableLiveData<>();
            loadUserCurrentProfile();
        }
        return userData;
    }

    private void loadUserCurrentProfile() {
        userData = repositoryFacade.getUserRepository()
                .getDocumentByUId(getCurrentUserUid(), User.class);
    }
}
