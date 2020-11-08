package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupervisedGroupListViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private MutableLiveData<List<Group>> groupLiveData;

    public SupervisedGroupListViewModel() {
        authRepository = new AuthRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
    }

    public LiveData<List<Group>> getSupervisedGroupList() {
        if (groupLiveData == null) {
            loadListOfDocumentByArrayContains();
        }
        return groupLiveData;
    }

    public LiveData<User> getSupervisor(String supervisorUid) {
        return userRepository.getDocumentByUId(supervisorUid, User.class);
    }

    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    private void loadListOfDocumentByArrayContains() {
        groupLiveData = groupRepository.getListOfDocumentByArrayContains("supervisedUserId",
                authRepository.getCurrentUser().getUid(), Group.class);
    }

}
