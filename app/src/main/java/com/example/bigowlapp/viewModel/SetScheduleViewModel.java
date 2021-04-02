package com.example.bigowlapp.viewModel;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.ReceiveScheduleNotification;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetScheduleViewModel extends BaseViewModel {

    private List<User> selectedUsers;
    private Group selectedGroup;
    private Group previousSelectedGroup;
    private CarmenFeature selectedLocation;

    private MutableLiveData<Schedule> newScheduleData;
    private LiveData<Group> selectedGroupData;
    private LiveData<CarmenFeature> selectedLocationData;

    private LiveData<List<User>> listOfUserInGroupData;
    private LiveData<List<User>> listOfSelectedUsersData;
    private MutableLiveData<List<Group>> listOfGroupData;

    public SetScheduleViewModel() {
        newScheduleData = new MutableLiveData<>(Schedule.getPrototypeSchedule());
        selectedGroupData = Transformations.map(newScheduleData, schedule -> selectedGroup);
        listOfUserInGroupData = Transformations.switchMap(selectedGroupData, group -> getListOfUsersFromGroup(group));
        selectedUsers = new ArrayList<>();
        listOfSelectedUsersData = Transformations.map(newScheduleData, schedule -> selectedUsers);
        selectedLocationData = Transformations.map(newScheduleData, schedule -> selectedLocation);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public SetScheduleViewModel(RepositoryFacade repositoryFacade) {
        this.repositoryFacade = repositoryFacade;
        newScheduleData = new MutableLiveData<>(Schedule.getPrototypeSchedule());
    }

    public MutableLiveData<Schedule> addSchedule() {
        Schedule schedule = newScheduleData.getValue();
        Map<String, UserScheduleResponse> userResponseMap =
                schedule.getMemberList().stream().collect(Collectors.toMap(
                        memberUid -> memberUid,
                        memberUid -> new UserScheduleResponse(Response.NEUTRAL, null))
                );
        schedule.setUserScheduleResponseMap(userResponseMap);

        //#32
        sendNewScheduleNotificationRequest();

        return repositoryFacade.getScheduleRepository().addDocument(schedule);
    }

    public LiveData<List<Group>> getListOfGroup() {
        if (listOfGroupData == null) {
            listOfGroupData = new MutableLiveData<>();
            loadListOfGroup();
        }
        return listOfGroupData;
    }

    private void loadListOfGroup() {
        String userId = getCurrentUserUid();
        listOfGroupData = repositoryFacade.getGroupRepository()
                .getListOfDocumentByAttribute(Group.Field.SUPERVISOR_ID, userId, Group.class);
    }

    public void updateScheduleTitle(String title) {
        newScheduleData.getValue().setTitle(title);
    }

    public void updateScheduleGroup(Group group) {
        this.selectedGroup = group;
        this.newScheduleData.getValue().setGroupUid(group.getUid());
        this.newScheduleData.getValue().setGroupSupervisorUid(group.getSupervisorId());

        this.selectedUsers = new ArrayList<>();
        this.newScheduleData.getValue().setMemberList(new ArrayList<>());
        notifyUi();
    }

    public void updateScheduleLocation(CarmenFeature location) {
        this.selectedLocation = location;
        GeoPoint locationCoords = new GeoPoint(location.center().latitude(), location.center().longitude());
        this.newScheduleData.getValue().setLocation(locationCoords);
        notifyUi();
    }

    public void updateSelectedUsers(List<User> users) {
        this.selectedUsers = users;
        List<String> listOfUserIds = new ArrayList<>();
        for (User selectedUser : selectedUsers) {
            listOfUserIds.add(selectedUser.getUid());
        }
        this.newScheduleData.getValue().setMemberList(listOfUserIds);
        notifyUi();
    }

    public void updateScheduleStartTime(Date date) {
        Timestamp timestamp = new Timestamp(date);
        newScheduleData.getValue().setStartTime(timestamp);
        notifyUi();
    }

    public void updateScheduleEndTime(Date date) {
        Timestamp timestamp = new Timestamp(date);
        newScheduleData.getValue().setEndTime(timestamp);
        notifyUi();
    }

    public LiveData<Schedule> getNewScheduleData() {
        return newScheduleData;
    }

    public LiveData<Group> getSelectedGroupData() {
        return selectedGroupData;
    }

    public LiveData<List<User>> getListOfUserInGroupData() {
        return listOfUserInGroupData;
    }

    public LiveData<List<User>> getListOfSelectedUsersData() {
        return listOfSelectedUsersData;
    }

    public LiveData<CarmenFeature> getSelectedLocationData() {
        return selectedLocationData;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public LiveData<List<User>> getListOfUsersFromGroup(Group group) {
        if (previousSelectedGroup != null && previousSelectedGroup.equals(group)) {
            return new MutableLiveData<>(listOfUserInGroupData.getValue());
        }
        previousSelectedGroup = group;
        if (group == null || group.getMemberIdList().isEmpty()) {
            return new MutableLiveData<>(new ArrayList<>());
        }
        return repositoryFacade.getUserRepository()
                .getListOfDocumentByArrayContains(User.Field.MEMBER_GROUP_ID_LIST, group.getUid(), User.class);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setNewScheduleData(MutableLiveData<Schedule> newScheduleData) {
        this.newScheduleData = newScheduleData;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setListOfGroupData(MutableLiveData<List<Group>> listOfGroupData) {
        this.listOfGroupData = listOfGroupData;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public CarmenFeature getSelectedLocation() {
        return selectedLocation;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setPreviousSelectedGroup(Group previousSelectedGroup) {
        this.previousSelectedGroup = previousSelectedGroup;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setListOfUserInGroupData(LiveData<List<User>> listOfUserInGroupData) {
        this.listOfUserInGroupData = listOfUserInGroupData;
    }

    private void notifyUi() {
        this.newScheduleData.setValue(this.newScheduleData.getValue());
    }

    private void sendNewScheduleNotificationRequest() { //#32
        for (User selectedUser : getSelectedUsers()) {
            ReceiveScheduleNotification newNotification = new ReceiveScheduleNotification();
            newNotification.setReceiverUid(selectedUser.getUid());
            newNotification.setSenderUid(getCurrentUserUid());
            newNotification.setGroupUid(getSelectedGroup().getUid());
            newNotification.setCreationTime(Timestamp.now());


            repositoryFacade.getNotificationRepository(selectedUser.getUid()).addDocument(newNotification);
        }
    }
}
