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
import java.util.Objects;

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

    public boolean isCurrentUserSet() {
        return authRepository.getCurrentUser() != null && authRepository.getCurrentUser().getUid() != null;
    }

    public LiveData<Group> getGroup() {
        if (selectedGroup == null) {
            selectedGroup = new MutableLiveData<>();
            loadGroup();
        }
        return selectedGroup;
    }

    public LiveData<List<User>> getUsersFromGroup(Group group) {
        if (usersInGroup == null) {
            usersInGroup = new MutableLiveData<>();
            loadAllUsersInGroup(group);
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

    public void removeUserFromGroup(User userToBeRemoved, List<User> userList) {
        Group groupWithRemovedUser = getGroup().getValue();
        Objects.requireNonNull(groupWithRemovedUser).getSupervisedUserId().remove(userToBeRemoved.getUId());
        userList.remove(userToBeRemoved);

        groupRepository.updateDocument(groupWithRemovedUser.getuId(), groupWithRemovedUser);

    }
}
