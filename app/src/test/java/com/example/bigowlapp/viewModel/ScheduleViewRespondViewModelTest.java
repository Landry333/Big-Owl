package com.example.bigowlapp.viewModel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleViewRespondViewModelTest {

    public static long ONE_SECOND = 1000;

    private ScheduleViewRespondViewModel scheduleViewRespondViewModel;
    private MutableLiveData<Schedule> testScheduleData;
    private User testUser;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository mockAuthRepository;
    @Mock
    private FirebaseUser mockTestFirebaseUser;

    @Before
    public void setUp() {
        // setup fake data
        testUser = new User("abc123", "first", "last", "+911", "test@mail.com", "url");
        testScheduleData = new MutableLiveData<>(createFakeSchedule());

        // setup mock responses
        when(mockAuthRepository.getCurrentUser()).thenReturn(mockTestFirebaseUser);
        when(mockTestFirebaseUser.getUid()).thenReturn("abc123");

        // setup viewModel to be tested
        scheduleViewRespondViewModel = new ScheduleViewRespondViewModel(mockAuthRepository, null, null);
        scheduleViewRespondViewModel.setScheduleData(testScheduleData);
    }

    @Test
    public void isOneMinuteAfterLastResponse() {
        Map<String, UserScheduleResponse> userScheduleResponseMap = testScheduleData.getValue().getUserScheduleResponseMap();

        // case where user did not respond yet
        UserScheduleResponse neutralResponse = new UserScheduleResponse(Response.NEUTRAL, null);
        userScheduleResponseMap.put(testUser.getUId(), neutralResponse);
        assertTrue(scheduleViewRespondViewModel.isOneMinuteAfterLastResponse());

        // case where the response is greater than a minute before
        Date lateTime = new Date();
        lateTime.setTime(lateTime.getTime() - 300 * ONE_SECOND);
        UserScheduleResponse oldResponse = new UserScheduleResponse(Response.ACCEPT, new Timestamp(lateTime));
        userScheduleResponseMap.put(testUser.getUId(), oldResponse);
        assertTrue(scheduleViewRespondViewModel.isOneMinuteAfterLastResponse());

        // case where the response is less than a minute before
        Date earlyTime = new Date();
        earlyTime.setTime(earlyTime.getTime() - 30 * ONE_SECOND);
        UserScheduleResponse quickResponse = new UserScheduleResponse(Response.REJECT, new Timestamp(earlyTime));
        userScheduleResponseMap.put(testUser.getUId(), quickResponse);
        assertFalse(scheduleViewRespondViewModel.isOneMinuteAfterLastResponse());

        // case where the response is slightly more than a minute
        Date closeTime = new Date();
        closeTime.setTime(closeTime.getTime() - 61 * ONE_SECOND);
        UserScheduleResponse oneMinuteResponse = new UserScheduleResponse(Response.ACCEPT, new Timestamp(closeTime));
        userScheduleResponseMap.put(testUser.getUId(), oneMinuteResponse);
        assertTrue(scheduleViewRespondViewModel.isOneMinuteAfterLastResponse());
    }

    private Schedule createFakeSchedule() {
        Map<String, UserScheduleResponse> userScheduleResponseMap = new HashMap<>();
        userScheduleResponseMap.put(testUser.getUId(), new UserScheduleResponse(Response.NEUTRAL, null));
        Schedule fakeSchedule = Schedule.getPrototypeSchedule();
        fakeSchedule.setUserScheduleResponseMap(userScheduleResponseMap);
        return fakeSchedule;
    }
}