package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class SetScheduleViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private Group selectedGroup;

    private final MutableLiveData<Schedule> newScheduleData;
    private final LiveData<Group> selectedGroupData;

    private MutableLiveData<List<User>> listOfUserData;
    private MutableLiveData<List<Group>> listOfGroupData;

    public SetScheduleViewModel() {
        authRepository = new AuthRepository();
        scheduleRepository = new ScheduleRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();

        newScheduleData = new MutableLiveData<>(Schedule.getPrototypeSchedule());
        selectedGroupData = Transformations.map(newScheduleData, schedule -> selectedGroup);
    }

    public LiveData<List<Group>> getListOfGroup() {
        if (listOfGroupData == null) {
            listOfGroupData = new MutableLiveData<>();
            loadListOfGroup();
        }
        return listOfGroupData;
    }

    public LiveData<List<User>> getListOfUsersFromGroup(Group group) {
        if (group.getSupervisedUserId().isEmpty()) {
            return listOfUserData = new MutableLiveData<>(new ArrayList<>());
        }
        if (selectedGroup == null || selectedGroup != group) {
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

    public LiveData<Schedule> getNewScheduleData() {
        return newScheduleData;
    }

    public LiveData<Group> getSelectedGroupData() {
        return selectedGroupData;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void notifyUi() {
        this.newScheduleData.setValue(this.newScheduleData.getValue());
    }

    public void updateScheduleTitle(String title) {
        newScheduleData.getValue().setTitle(title);
    }

    // TODO: Maybe just pass the id instead???
    public void updateCurrentGroup(Group group) {
        this.selectedGroup = group;
        this.newScheduleData.getValue().setGroupId(group.getuId());
        notifyUi();
    }

    // TODO: Update dates in date binding kind of way as well ???
//    public void updateStartTime(Date date) {
//        Timestamp timestamp = new Timestamp(date);
//        newScheduleData.getValue().setStartTime(timestamp);
//        notifyUi();
//    }
//
//    public void updateEndTime(Date date) {
//        Timestamp timestamp = new Timestamp(date);
//        newScheduleData.getValue().setEndTime(timestamp);
//        notifyUi();
//    }
}
