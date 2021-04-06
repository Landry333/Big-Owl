package com.example.bigowlapp.viewModel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetScheduleViewModelTest {

    private SetScheduleViewModel setScheduleViewModel;
    private LiveDataWithStatus<Schedule> testScheduleData;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RepositoryFacade repositoryFacade;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @Before
    public void setUp() {
        when(repositoryFacade.getScheduleRepository()).thenReturn(scheduleRepository);
        when(repositoryFacade.getGroupRepository()).thenReturn(groupRepository);
        when(repositoryFacade.getUserRepository()).thenReturn(userRepository);
        when(repositoryFacade.getNotificationRepository(anyString())).thenReturn(notificationRepository);
        when(repositoryFacade.getCurrentUserUid()).thenReturn("123");

        setScheduleViewModel = new SetScheduleViewModel(repositoryFacade);
    }

    @Test
    public void addSchedule() {
        List<String> memberIds = Arrays.asList("joe", "doe", "john");

        Schedule schedule = Schedule.getPrototypeSchedule();
        schedule.setUid("Testing");
        schedule.setMemberList(memberIds);
        testScheduleData = new LiveDataWithStatus<>(schedule);
        setScheduleViewModel.setNewScheduleData(testScheduleData);
        when(scheduleRepository.addDocument(schedule)).thenReturn(testScheduleData);

        List<User> userList = new ArrayList<>();
        for (String userId : memberIds) {
            User user = new User();
            user.setUid(userId);
            user.setFirstName(userId);
            user.setLastName(userId + "ly");
            userList.add(user);
        }
        setScheduleViewModel.setSelectedUsers(userList);

        Group group = new Group();
        group.setUid("GroupId");
        group.setName("Group_Name");
        group.setSupervisorId("MonitoringId");
        setScheduleViewModel.setSelectedGroup(group);
        setScheduleViewModel.setSelectedGroupData(new MutableLiveData<>(group));

        Schedule returnedSchedule = setScheduleViewModel.addSchedule().getValue();

        verify(scheduleRepository).addDocument(schedule);
        assertEquals(schedule.getUid(), returnedSchedule.getUid());
        assertEquals(schedule.getMemberList(), returnedSchedule.getMemberList());
        assertEquals(schedule.getMemberList().size(), returnedSchedule.getUserScheduleResponseMap().size());
        assertEquals(Response.NEUTRAL, returnedSchedule.getUserScheduleResponseMap().get("joe").getResponse());
        assertEquals(Response.NEUTRAL, returnedSchedule.getUserScheduleResponseMap().get("doe").getResponse());
        assertEquals(Response.NEUTRAL, returnedSchedule.getUserScheduleResponseMap().get("john").getResponse());
        assertNull(returnedSchedule.getUserScheduleResponseMap().get("joe").getResponseTime());
        assertNull(returnedSchedule.getUserScheduleResponseMap().get("doe").getResponseTime());
        assertNull(returnedSchedule.getUserScheduleResponseMap().get("john").getResponseTime());

        verify(notificationRepository, times(memberIds.size())).addDocument(any(Notification.class));
    }

    @Test
    public void getListOfGroup() {
        setScheduleViewModel.setListOfGroupData(null);
        when(groupRepository.getListOfDocumentByAttribute(Group.Field.SUPERVISOR_ID, "123", Group.class)).thenReturn(new LiveDataWithStatus<>());
        assertNotNull(setScheduleViewModel.getListOfGroup());
        assertNotNull(setScheduleViewModel.getListOfGroup());
        verify(groupRepository, times(1)).getListOfDocumentByAttribute(Group.Field.SUPERVISOR_ID, "123", Group.class);
    }

    @Test
    public void updateScheduleTitle() {
        String title = "My Title";
        setScheduleViewModel.updateScheduleTitle(title);
        assertEquals(title, setScheduleViewModel.getNewScheduleData().getValue().getTitle());
    }

    @Test
    public void updateScheduleGroup() {
        Group group = new Group();
        group.setUid("GroupId");
        group.setSupervisorId("MonitoringId");

        setScheduleViewModel.updateScheduleGroup(group);

        Schedule returnedSchedule = setScheduleViewModel.getNewScheduleData().getValue();

        assertEquals(group.getUid(), returnedSchedule.getGroupUid());
        assertEquals(group.getSupervisorId(), returnedSchedule.getGroupSupervisorUid());
        assertEquals(new ArrayList<>(), returnedSchedule.getMemberList());
        assertEquals(group, setScheduleViewModel.getSelectedGroup());
        assertEquals(new ArrayList<>(), setScheduleViewModel.getSelectedUsers());
    }

    @Test
    public void updateScheduleLocation() {
        CarmenFeature location = CarmenFeature.builder()
                .rawCenter(new double[]{-88.00, 88.00})
                .build();
        GeoPoint locationCoordinate = new GeoPoint(location.center().latitude(),
                location.center().longitude());

        setScheduleViewModel.updateScheduleLocation(location);

        assertEquals(location, setScheduleViewModel.getSelectedLocation());
        assertEquals(locationCoordinate, setScheduleViewModel.getNewScheduleData().getValue().getLocation());
    }

    @Test
    public void updateSelectedUsers() {
        List<User> userList = new ArrayList<>();
        List<String> userIdsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUid(Integer.toString(i));
            userList.add(user);
            userIdsList.add(Integer.toString(i));
        }

        setScheduleViewModel.updateSelectedUsers(userList);

        assertEquals(userList, setScheduleViewModel.getSelectedUsers());
        assertEquals(userIdsList, setScheduleViewModel.getNewScheduleData().getValue().getMemberList());
    }

    @Test
    public void updateScheduleTime() {
        Timestamp startTime = new Timestamp(Calendar.getInstance().getTime());
        setScheduleViewModel.updateScheduleStartTime(startTime.toDate());
        assertEquals(startTime, setScheduleViewModel.getNewScheduleData().getValue().getStartTime());

        Timestamp endTime = new Timestamp(Calendar.getInstance().getTime());
        setScheduleViewModel.updateScheduleEndTime(endTime.toDate());
        assertEquals(endTime, setScheduleViewModel.getNewScheduleData().getValue().getEndTime());
    }

    @Test
    public void getListOfUsersFromGroup() {
        List<User> userList = new ArrayList<>();
        List<String> userIdsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUid(Integer.toString(i));
            userList.add(user);
            userIdsList.add(Integer.toString(i));
        }

        Group group = new Group();
        group.setMemberIdList(userIdsList);

        // case where same group already loaded is set
        setScheduleViewModel.setListOfUserInGroupData(new MutableLiveData<>(userList));
        setScheduleViewModel.setPreviousSelectedGroup(group);
        List<User> scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(group).getValue();
        assertEquals(userList, scheduleUserList);

        // case where the group is null
        scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(null).getValue();
        assertEquals(new ArrayList<>(), scheduleUserList);

        // case where data was not loaded yet
        setScheduleViewModel.setPreviousSelectedGroup(null);
        when(userRepository.getListOfDocumentByArrayContains(User.Field.MEMBER_GROUP_ID_LIST, group.getUid(), User.class)).thenReturn(new LiveDataWithStatus<>(userList));
        scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(group).getValue();
        verify(userRepository).getListOfDocumentByArrayContains(User.Field.MEMBER_GROUP_ID_LIST, group.getUid(), User.class);
        assertEquals(userList, scheduleUserList);

        // case where the group is empty
        setScheduleViewModel.setPreviousSelectedGroup(null);
        group.setMemberIdList(new ArrayList<>());
        scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(group).getValue();
        assertEquals(new ArrayList<>(), scheduleUserList);
    }
}