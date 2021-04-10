package com.example.bigowlapp.service;

import android.content.Context;
import android.content.Intent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.utils.AuthenticatorByPhoneNumber;
import com.example.bigowlapp.utils.LocationTrackingExpiredAlarmManager;
import com.example.bigowlapp.utils.PeriodicLocationCheckAlarmManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationBroadcastReceiverTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RepositoryFacade mockRepositoryFacade;
    @Mock
    private ScheduleRepository mockScheduleRepository;
    @Mock
    private Intent mockGefenceIntent;
    @Mock
    private GeofencingEvent mockGeofencingEvent;
    @Mock
    private Context mockContext;
    @Mock
    private AuthenticatorByPhoneNumber mockAuthenticatorByPhoneNumber;
    @Mock
    private PeriodicLocationCheckAlarmManager mockLocationCheckAlarmManager;
    @Mock
    private LocationTrackingExpiredAlarmManager mockLocationTrackingExpiredAlarmManager;
    @Mock
    private GeofencingClient mockGeofencingClient;


    private Schedule schedule;
    private LiveDataWithStatus<List<Schedule>> scheduleListData;

    private LocationBroadcastReceiver locationBroadcastReceiver;


    @Before
    public void setUp() throws Exception {
        schedule = createScheduleWithAnAttendance(Attendance.LocatedStatus.NOT_DETECTED);
        scheduleListData = new LiveDataWithStatus<>(Collections.singletonList(schedule));

        when(mockRepositoryFacade.getScheduleRepository()).thenReturn(mockScheduleRepository);
        when(mockRepositoryFacade.getCurrentUserUid()).thenReturn("userUid");
        when(mockScheduleRepository.getDocumentsByListOfUid(anyList(), any())).thenReturn(scheduleListData);

        when(mockGeofencingEvent.getTriggeringGeofences()).thenReturn(Collections.singletonList(() -> "scheduleUid"));

        locationBroadcastReceiver = new LocationBroadcastReceiver();
        locationBroadcastReceiver.setRepositoryFacade(mockRepositoryFacade);
        locationBroadcastReceiver.setGeofencingEvent(mockGeofencingEvent);
        locationBroadcastReceiver.setAuthenticatorByPhoneNumber(mockAuthenticatorByPhoneNumber);
        locationBroadcastReceiver.setLocationCheckAlarmManager(mockLocationCheckAlarmManager);
        locationBroadcastReceiver.setLocationTrackingExpiredAlarmManager(mockLocationTrackingExpiredAlarmManager);
        locationBroadcastReceiver.setGeofencingClient(mockGeofencingClient);
    }

    @Test
    public void onReceiveGetsGeofencingError() {
        when(mockGeofencingEvent.hasError()).thenReturn(true);
        when(mockGeofencingEvent.getErrorCode()).thenReturn(GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION);

        locationBroadcastReceiver.onReceive(mockContext, mockGefenceIntent);
        verify(mockGeofencingEvent, times(1)).getErrorCode();
    }

    @Test
    public void onReceiveGetsGeofencingCorrectLocation() {
        when(mockGeofencingEvent.hasError()).thenReturn(false);
        when(mockGeofencingEvent.getGeofenceTransition()).thenReturn(Geofence.GEOFENCE_TRANSITION_ENTER);

        locationBroadcastReceiver.onReceive(mockContext, mockGefenceIntent);

        Attendance attendanceResult = getUserUid();
        assertEquals(Attendance.LocatedStatus.CORRECT_LOCATION, attendanceResult.getScheduleLocated());
        verify(mockLocationCheckAlarmManager).cancelPeriodicLocationCheck();
        verify(mockLocationTrackingExpiredAlarmManager).cancelExpirationAlarm();
        verify(mockGeofencingClient).removeGeofences(Collections.singletonList(schedule.getUid()));
        verify(mockAuthenticatorByPhoneNumber).authenticate(schedule.getUid());
        verify(mockScheduleRepository).updateDocument(schedule.getUid(), schedule);
        verify(mockAuthenticatorByPhoneNumber).authenticate(schedule.getUid());
    }

    @Test
    public void onReceiveGetsGeofencingWrongLocation() {
        when(mockGeofencingEvent.hasError()).thenReturn(false);
        when(mockGeofencingEvent.getGeofenceTransition()).thenReturn(Geofence.GEOFENCE_TRANSITION_EXIT);


        locationBroadcastReceiver.onReceive(mockContext, mockGefenceIntent);

        Attendance attendanceResult = getUserUid();
        assertEquals(Attendance.LocatedStatus.WRONG_LOCATION, attendanceResult.getScheduleLocated());
        verify(mockScheduleRepository).updateDocument(schedule.getUid(), schedule);
    }

    @Test
    public void onReceiveGetsGeofencingNotDetectedLocation() {
        when(mockGeofencingEvent.hasError()).thenReturn(false);
        when(mockGeofencingEvent.getGeofenceTransition()).thenReturn(0);

        locationBroadcastReceiver.onReceive(mockContext, mockGefenceIntent);

        Attendance attendanceResult = getUserUid();
        assertEquals(Attendance.LocatedStatus.NOT_DETECTED, attendanceResult.getScheduleLocated());
        assertEquals("fakeEvent", schedule.getEvent());
        verify(mockScheduleRepository).updateDocument(schedule.getUid(), schedule);
    }

    @Test
    public void onReceiveStillCorrectLocationIfInitiallyCorrectLocation() {
        when(mockGeofencingEvent.hasError()).thenReturn(false);
        when(mockGeofencingEvent.getGeofenceTransition()).thenReturn(0);
        schedule = createScheduleWithAnAttendance(Attendance.LocatedStatus.CORRECT_LOCATION);
        scheduleListData.postValue(Collections.singletonList(schedule));

        locationBroadcastReceiver.onReceive(mockContext, mockGefenceIntent);

        Attendance attendanceResult = getUserUid();
        assertEquals(Attendance.LocatedStatus.CORRECT_LOCATION, attendanceResult.getScheduleLocated());
        verify(mockScheduleRepository).updateDocument(schedule.getUid(), schedule);
        verify(mockAuthenticatorByPhoneNumber).authenticate(schedule.getUid());
    }

    private Schedule createScheduleWithAnAttendance(Attendance.LocatedStatus initialStatus) {
        Attendance attendance = new Attendance();
        attendance.setScheduleLocated(initialStatus);

        UserScheduleResponse response = new UserScheduleResponse();
        response.setAttendance(attendance);

        Map<String, UserScheduleResponse> responseMap = new HashMap<>();
        responseMap.put("userUid", response);

        Schedule schedule = new Schedule();
        schedule.setUid("scheduleUid");
        schedule.setEvent("fakeEvent");
        schedule.setUserScheduleResponseMap(responseMap);

        return schedule;
    }

    private Attendance getUserUid() {
        return schedule.getUserScheduleResponseMap()
                .get("userUid")
                .getAttendance();
    }
}