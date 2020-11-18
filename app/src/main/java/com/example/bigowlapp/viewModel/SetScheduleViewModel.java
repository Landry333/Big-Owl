package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;

import java.util.List;

public class SetScheduleViewModel extends ViewModel {

    private AuthRepository authRepository;
    private ScheduleRepository scheduleRepository;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    private MutableLiveData<Schedule> scheduleData;
    private MutableLiveData<List<User>> listOfUserData;
    private MutableLiveData<List<Group>> listOfGroupData;

    public SetScheduleViewModel() {
        authRepository = new AuthRepository();
        scheduleRepository = new ScheduleRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
    }

    public LiveData<List<Group>> getListOfGroup() {
        if (listOfGroupData == null) {
            listOfGroupData = new MutableLiveData<>();
            loadListOfGroup();
        }
        return listOfGroupData;
    }

    public LiveData<List<User>> getListOfUsersFromGroup(Group group) {
        if (listOfUserData == null) {
            listOfUserData = new MutableLiveData<>();
            loadUsers(group);
        }
        return listOfUserData;
    }

    public void loadListOfGroup() {
        String userId = authRepository.getCurrentUser().getUid();
        listOfGroupData = groupRepository.getListOfDocumentByAttribute("monitoringUserId", userId, Group.class);
    }

    public void loadUsers(Group group) {
        listOfUserData = userRepository.getDocumentsByListOfUId(group.getSupervisedUserId(), User.class);
    }

    public boolean isCurrentUserSet() {
        return authRepository.getCurrentUser() != null;
    }
}
