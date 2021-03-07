package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SupervisedGroupListViewModel extends BaseViewModel {
    private MutableLiveData<List<Group>> groupLiveData;
    private MutableLiveData<User> userData;

    public SupervisedGroupListViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public LiveData<List<Group>> getSupervisedGroupListData() {
        if (groupLiveData == null) {
            loadListOfDocumentByArrayContains();
        }
        return groupLiveData;
    }

    private void loadListOfDocumentByArrayContains() {
        groupLiveData = repositoryFacade.getGroupRepository()
                .getListOfDocumentByArrayContains("memberIdList",
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
                .getDocumentByUid(getCurrentUserUid(), User.class);
    }
}
