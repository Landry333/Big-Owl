package com.example.bigowlapp.viewModel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;

import java.util.List;

public class SupervisedGroupListViewModel extends ViewModel {

    private AuthRepository authRepository;
    private GroupRepository groupRepository;
    private MutableLiveData<List<Group>> groupLiveData;

    @ViewModelInject
    public SupervisedGroupListViewModel(AuthRepository authRepository, GroupRepository groupRepository) {
        this.authRepository = authRepository;
        this.groupRepository = groupRepository;
    }

    public MutableLiveData<List<Group>> setListOfDocumentByArrayContains() {
        groupLiveData = groupRepository.getListOfDocumentByArrayContains("supervisedUserId",
                authRepository.getCurrentUser().getUid(), Group.class);
        return groupLiveData;
    }


}
