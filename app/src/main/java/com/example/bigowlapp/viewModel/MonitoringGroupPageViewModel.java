package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;

import java.util.List;
import java.util.Objects;

public class MonitoringGroupPageViewModel extends BaseViewModel {

    private MutableLiveData<Group> selectedGroup;
    private MutableLiveData<List<User>> usersInGroup;

    public MonitoringGroupPageViewModel() {
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
        String userId = getCurrentUserUid();
        selectedGroup = repositoryFacade.getGroupRepository()
                .getDocumentByAttribute("monitoringUserId", userId, Group.class);
    }

    private void loadAllUsersInGroup(Group group) {
        usersInGroup = repositoryFacade.getUserRepository()
                .getDocumentsByListOfUid(group.getSupervisedUserId(), User.class);
    }

    public void removeUserFromGroup(User userToBeRemoved) {
        Group groupWithRemovedUser = getGroup().getValue();
        Objects.requireNonNull(groupWithRemovedUser).getSupervisedUserId().remove(userToBeRemoved.getUid());

        repositoryFacade.getGroupRepository()
                .updateDocument(groupWithRemovedUser.getUid(), groupWithRemovedUser);
    }
}
