package com.example.bigowlapp.viewModel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import org.junit.After;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetScheduleViewModelTest {

    private SetScheduleViewModel setScheduleViewModel;
    private MutableLiveData<Schedule> testScheduleData;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository authRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FirebaseUser testFirebaseUser;

    @Before
    public void setUp() throws Exception {
        setScheduleViewModel = new SetScheduleViewModel(authRepository, scheduleRepository,
                groupRepository, userRepository);
        when(authRepository.getCurrentUser()).thenReturn(testFirebaseUser);
        when(testFirebaseUser.getUid()).thenReturn("123");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addSchedule() {
        Schedule schedule = Schedule.getPrototypeSchedule();
        schedule.setuId("Testing");
        schedule.setMemberList(Arrays.asList("joe", "doe", "john"));

        testScheduleData = new MutableLiveData<>(schedule);
        setScheduleViewModel.setNewScheduleData(testScheduleData);
        when(scheduleRepository.addDocument(schedule)).thenReturn(testScheduleData);

        Schedule returnedSchedule = setScheduleViewModel.addSchedule().getValue();

        verify(scheduleRepository).addDocument(schedule);
        assertEquals(schedule.getuId(), returnedSchedule.getuId());
        assertEquals(schedule.getMemberList(), returnedSchedule.getMemberList());
        assertEquals(schedule.getMemberList().size(), returnedSchedule.getMembers().size());
        assertEquals(Response.NEUTRAL, returnedSchedule.getMembers().get("joe").getResponse());
        assertEquals(Response.NEUTRAL, returnedSchedule.getMembers().get("doe").getResponse());
        assertEquals(Response.NEUTRAL, returnedSchedule.getMembers().get("john").getResponse());
        assertNull(returnedSchedule.getMembers().get("joe").getResponseTime());
        assertNull(returnedSchedule.getMembers().get("doe").getResponseTime());
        assertNull(returnedSchedule.getMembers().get("john").getResponseTime());
    }

    @Test
    public void getListOfGroup() {
        setScheduleViewModel.setListOfGroupData(null);
        when(groupRepository.getListOfDocumentByAttribute("monitoringUserId", "123", Group.class)).thenReturn(new MutableLiveData<>());
        assertNotNull(setScheduleViewModel.getListOfGroup());
        assertNotNull(setScheduleViewModel.getListOfGroup());
        verify(groupRepository, times(1)).getListOfDocumentByAttribute("monitoringUserId", "123", Group.class);
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
        group.setuId("GroupId");
        group.setMonitoringUserId("MonitoringId");

        setScheduleViewModel.updateScheduleGroup(group);

        Schedule returnedSchedule = setScheduleViewModel.getNewScheduleData().getValue();

        assertEquals(group.getuId(), returnedSchedule.getGroupUId());
        assertEquals(group.getMonitoringUserId(), returnedSchedule.getGroupSupervisorUId());
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
            user.setUId(Integer.toString(i));
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
        setScheduleViewModel.updateScheduleStartTime(endTime.toDate());
        assertEquals(endTime, setScheduleViewModel.getNewScheduleData().getValue().getStartTime());
    }

    @Test
    public void getListOfUsersFromGroup() {
        List<User> userList = new ArrayList<>();
        List<String> userIdsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUId(Integer.toString(i));
            userList.add(user);
            userIdsList.add(Integer.toString(i));
        }

        Group group = new Group();
        group.setSupervisedUserId(userIdsList);

        // case where same group already loaded is set
        setScheduleViewModel.setListOfUserInGroupData(new MutableLiveData<>(userList));
        setScheduleViewModel.setPreviousSelectedGroup(group);
        List<User> scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(group).getValue();
        assertEquals(userList, scheduleUserList);

        // case where the group is either null or has no users
        scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(null).getValue();
        assertEquals(new ArrayList<>(), scheduleUserList);

        // TODO: empty list case

        // case where data was not loaded yet
        setScheduleViewModel.setPreviousSelectedGroup(null);
        when(userRepository.getDocumentsByListOfUId(userIdsList, User.class)).thenReturn(new MutableLiveData<>(userList));
        scheduleUserList = setScheduleViewModel.getListOfUsersFromGroup(group).getValue();
        verify(userRepository).getDocumentsByListOfUId(userIdsList, User.class);
        assertEquals(userList, scheduleUserList);

    }
}