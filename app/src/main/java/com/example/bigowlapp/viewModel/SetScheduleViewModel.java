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
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetScheduleViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private Group selectedGroup;
    private Group previousSelectedGroup;
    private Map<User, Boolean> selectableUsers;
    private CarmenFeature selectedLocation;

    private final MutableLiveData<Schedule> newScheduleData;
    private final LiveData<Group> selectedGroupData;
    private final LiveData<CarmenFeature> selectedLocationData;

    private LiveData<List<User>> listOfUserInGroupData;
    private LiveData<Map<User, Boolean>> selectableUsersData;
    private MutableLiveData<List<Group>> listOfGroupData;

    public SetScheduleViewModel() {
        authRepository = new AuthRepository();
        scheduleRepository = new ScheduleRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();

        newScheduleData = new MutableLiveData<>(Schedule.getPrototypeSchedule());
        selectedGroupData = Transformations.map(newScheduleData, schedule -> selectedGroup);
        listOfUserInGroupData = Transformations.switchMap(selectedGroupData, group -> getListOfUsersFromGroup(group));
        selectableUsersData = Transformations.map(listOfUserInGroupData, users -> selectableUsers);
        selectedLocationData = Transformations.map(newScheduleData, schedule -> selectedLocation);
    }

    public LiveData<List<Group>> getListOfGroup() {
        if (listOfGroupData == null) {
            listOfGroupData = new MutableLiveData<>();
            loadListOfGroup();
        }
        return listOfGroupData;
    }

    public void loadListOfGroup() {
        String userId = authRepository.getCurrentUser().getUid();
        listOfGroupData = groupRepository.getListOfDocumentByAttribute("monitoringUserId", userId, Group.class);
    }

    public void loadUsers(Group group) {
        listOfUserInGroupData = userRepository.getDocumentsByListOfUId(group.getSupervisedUserId(), User.class);
    }

    public void updateScheduleTitle(String title) {
        newScheduleData.getValue().setTitle(title);
    }

    public void updateScheduleGroup(Group group) {
        this.selectedGroup = group;
        this.newScheduleData.getValue().setGroupId(group.getuId());
        notifyUi();
    }

    public void updateScheduleLocation(CarmenFeature location) {
        this.selectedLocation = location;
        GeoPoint locationCoords = new GeoPoint(location.center().latitude(), location.center().longitude());
        this.newScheduleData.getValue().setLocation(locationCoords);
        notifyUi();
    }

    // TODO: Update dates in date binding kind of way as well ???
//    public void updateScheduleStartTime(Date date) {
//        Timestamp timestamp = new Timestamp(date);
//        newScheduleData.getValue().setStartTime(timestamp);
//        notifyUi();
//    }
//
//    public void updateScheduleEndTime(Date date) {
//        Timestamp timestamp = new Timestamp(date);
//        newScheduleData.getValue().setEndTime(timestamp);
//        notifyUi();
//    }

    public LiveData<Schedule> getNewScheduleData() {
        return newScheduleData;
    }

    public LiveData<Group> getSelectedGroupData() {
        return selectedGroupData;
    }

    public LiveData<List<User>> getListOfUserInGroupData() {
        return listOfUserInGroupData;
    }

    public LiveData<Map<User, Boolean>> getSelectableUsersData() {
        return selectableUsersData;
    }

    public LiveData<CarmenFeature> getSelectedLocationData() {
        return selectedLocationData;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public boolean isCurrentUserSet() {
        return authRepository.getCurrentUser() != null;
    }

    public void notifyUi() {
        this.newScheduleData.setValue(this.newScheduleData.getValue());
    }

    private LiveData<List<User>> getListOfUsersFromGroup(Group group) {
        if (previousSelectedGroup != null && previousSelectedGroup.equals(group)) {
            return new MutableLiveData<>(listOfUserInGroupData.getValue());
        }
        previousSelectedGroup = group;
        if (group == null || group.getSupervisedUserId().isEmpty()) {
            return new MutableLiveData<>(new ArrayList<>());
        }
        return userRepository.getDocumentsByListOfUId(group.getSupervisedUserId(), User.class);
    }

    public void initializeSelectableUsersMap(List<User> users) {
        Map<User, Boolean> newSelectableUserMap = new HashMap<>();
        for (User user : users) {
            newSelectableUserMap.put(user, false);
        }

        selectableUsers = newSelectableUserMap;
    }
}
