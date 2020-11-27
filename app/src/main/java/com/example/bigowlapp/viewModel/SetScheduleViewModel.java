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
import java.util.List;

public class SetScheduleViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private Group selectedGroup;
    private CarmenFeature selectedLocation;

    private final MutableLiveData<Schedule> newScheduleData;
    private final LiveData<Group> selectedGroupData;
    private final LiveData<CarmenFeature> selectedLocationData;

    private MutableLiveData<List<User>> listOfUserData;
    private MutableLiveData<List<Group>> listOfGroupData;

    public SetScheduleViewModel() {
        authRepository = new AuthRepository();
        scheduleRepository = new ScheduleRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();

        newScheduleData = new MutableLiveData<>(Schedule.getPrototypeSchedule());
        selectedGroupData = Transformations.map(newScheduleData, schedule -> selectedGroup);
        selectedLocationData = Transformations.map(newScheduleData, schedule -> selectedLocation);
        // TODO: use switchMap() to get users instead
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
}
