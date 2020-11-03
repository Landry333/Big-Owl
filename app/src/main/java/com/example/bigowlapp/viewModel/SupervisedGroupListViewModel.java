package com.example.bigowlapp.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;

import java.util.List;

public class SupervisedGroupListViewModel {

    private AuthRepository authRepository;
    private GroupRepository groupRepository;
    private MutableLiveData<List<Group>> groupLiveData;

    public SupervisedGroupListViewModel() {
        authRepository = new AuthRepository();
        groupRepository = new GroupRepository();
    }

    public MutableLiveData<List<Group>> setListOfDocumentByArrayContains() {
        groupLiveData = groupRepository.getListOfDocumentByArrayContains("supervisedUserId",
                authRepository.getCurrentUser().getUid(), Group.class);
        return groupLiveData;
    }


}
