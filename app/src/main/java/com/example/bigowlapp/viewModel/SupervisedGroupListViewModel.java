package com.example.bigowlapp.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SupervisedGroupListViewModel extends ViewModel {

    private AuthRepository authRepository;
    private GroupRepository groupRepository;
    private MutableLiveData<List<Group>> groupLiveData;

    public SupervisedGroupListViewModel() {
        authRepository = new AuthRepository();
        groupRepository = new GroupRepository();
    }

    public MutableLiveData<List<Group>> getSupervisedGroupList() {
        if (groupLiveData == null) {
            loadListOfDocumentByArrayContains();
        }
        return groupLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    private void loadListOfDocumentByArrayContains() {
        groupLiveData = groupRepository.getListOfDocumentByArrayContains("supervisedUserId",
                authRepository.getCurrentUser().getUid(), Group.class);
    }
}
