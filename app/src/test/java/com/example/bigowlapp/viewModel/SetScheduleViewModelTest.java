package com.example.bigowlapp.viewModel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

    @Before
    public void setUp() throws Exception {
        setScheduleViewModel = new SetScheduleViewModel(authRepository, scheduleRepository,
                groupRepository, userRepository);
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
    }

    @Test
    public void loadListOfGroup() {
    }

    @Test
    public void loadUsers() {
    }

    @Test
    public void updateScheduleTitle() {
    }

    @Test
    public void updateScheduleGroup() {
    }

    @Test
    public void updateScheduleLocation() {
    }

    @Test
    public void updateSelectedUsers() {
    }

    @Test
    public void updateScheduleStartTime() {
    }

    @Test
    public void updateScheduleEndTime() {
    }

    @Test
    public void isCurrentUserSet() {
    }

    @Test
    public void notifyUi() {
    }

    @Test
    public void getListOfUsersFromGroup() {

    }
}