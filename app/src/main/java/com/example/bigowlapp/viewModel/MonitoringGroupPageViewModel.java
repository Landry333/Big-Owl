package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.UserRepository;

import java.util.List;

public class MonitoringGroupPageViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private MutableLiveData<Group> selectedGroup;
    private MutableLiveData<List<User>> usersInGroup;

    public MonitoringGroupPageViewModel() {
        authRepository = new AuthRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
    }

    public LiveData<Group> getGroup() {
        if (selectedGroup == null) {
            selectedGroup = new MutableLiveData<>();
            loadGroup();
        }
        return selectedGroup;
    }

    public LiveData<List<User>> getUsers() {
        if (usersInGroup == null) {
            usersInGroup = new MutableLiveData<>();
            loadAllUsersInGroup(selectedGroup.getValue());
        }
        return usersInGroup;
    }

    private void loadGroup() {
        String userId = authRepository.getCurrentUser().getUid();
        selectedGroup = groupRepository.getDocumentByAttribute("monitoringUserId", userId, Group.class);
    }

    private void loadAllUsersInGroup(Group group) {
        usersInGroup = userRepository.getDocumentsByUId(group.getSupervisedUserId(), User.class);
    }

}
