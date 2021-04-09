package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.view_model.ScheduleViewRespondViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduleViewRespondActivityTest {

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION);

    @Mock
    private ScheduleViewRespondViewModel mockScheduleViewRespondViewModel;

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private NotificationRepository mockNotificationRepository;

    @Mock
    private FirebaseUser testFirebaseCurrentUser;

    private ActivityScenario<ScheduleViewRespondActivity> activityScenario;
    private Schedule testSchedule;

    @Before
    public void setUp() {
        initMocks(this);

        User testSupervisor = new User(
                "supervisor001",
                "supervisor",
                "001",
                "+1234567890",
                "testSupervisor@mail.com",
                null,
                null);
        User testCurrentUser = new User(
                "testCurrentUser001",
                "test",
                "currentUser",
                "+1111111111",
                "testCurrentUser@mail.com",
                null,
                null);

        Map<String, UserScheduleResponse> testScheduleMembersMap = new HashMap<>();
        testScheduleMembersMap.put(testCurrentUser.getUid(),
                new UserScheduleResponse(Response.NEUTRAL, null));
        List<String> testScheduleMemberList = new ArrayList<>();
        testScheduleMemberList.add(0, testCurrentUser.getUid());
        testSchedule = new Schedule();
        testSchedule.setUid("schedule001");
        testSchedule.setTitle("testSchedule001");
        testSchedule.setEvent("testEvent001");
        testSchedule.setGroupUid("testGroup001");
        testSchedule.setGroupSupervisorUid(testSupervisor.getUid());
        testSchedule.setStartTime(Timestamp.now());
        testSchedule.setEndTime(new Timestamp(Timestamp.now().getSeconds() + 600000, 0));
        testSchedule.setLocation(new GeoPoint(0, 0));
        testSchedule.setUserScheduleResponseMap(testScheduleMembersMap);
        Intent testIntent = new Intent(ApplicationProvider.getApplicationContext(), ScheduleViewRespondActivity.class);
        testIntent.putExtra("scheduleUid", testSchedule.getUid());
        testIntent.putExtra("groupName", "test group");
        testIntent.putExtra("supervisorName", testSupervisor.getFullName());

        MutableLiveData<Schedule> testScheduleData = new MutableLiveData<>(testSchedule);
        testScheduleData.postValue(testSchedule);

        when(testFirebaseCurrentUser.getUid()).thenReturn(testCurrentUser.getUid());
        when(mockAuthRepository.getCurrentUser()).thenReturn(testFirebaseCurrentUser);
        when(mockAuthRepository.getCurrentUser().getUid()).thenReturn(testCurrentUser.getUid());
        when(mockNotificationRepository.addDocument(any())).thenReturn(null);
        when(mockScheduleViewRespondViewModel.isCurrentUserSet()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.getCurrentScheduleData(anyString())).thenReturn(testScheduleData);
        when(mockScheduleViewRespondViewModel.isCurrentUserInSchedule()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.isOneMinuteAfterLastResponse()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.NEUTRAL, null));
        doAnswer(a -> {
            testScheduleMembersMap.put(testCurrentUser.getUid(), mockScheduleViewRespondViewModel.getCurrentUserNewResponse());
            testSchedule.getUserScheduleResponseMap().put(testCurrentUser.getUid(), mockScheduleViewRespondViewModel.getCurrentUserNewResponse());
            testScheduleData.postValue(testSchedule);
            mockScheduleViewRespondViewModel.notifySupervisorScheduleResponse();
            return null;
        }).when(mockScheduleViewRespondViewModel).respondSchedule(any(), any());

        activityScenario = ActivityScenario.launch(testIntent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity ->
                activity.setScheduleViewRespondViewModel(mockScheduleViewRespondViewModel));
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void scheduleWithNoRespondedBeforeTest() {
        verify(mockScheduleViewRespondViewModel, times(1)).getCurrentScheduleData(testSchedule.getUid());
        verify(mockScheduleViewRespondViewModel, times(1)).isCurrentUserInSchedule();
        onView(withId(R.id.linear_layout_schedule_view)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_group_uid)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_group_supervisor_name)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_start_time)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_end_time)).check(matches(isDisplayed()));
        onView(withId(R.id.view_divider_below_schedule)).check(matches(isDisplayed()));
        onView(withId(R.id.linear_layout_response)).check(matches(not(isDisplayed())));
        onView(withId(R.id.line_below_response)).check(matches(not(isDisplayed())));
        verify(mockScheduleViewRespondViewModel, times(1)).getUserScheduleResponse();
        onView(withId(R.id.button_accept)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reject)).check(matches(isDisplayed()));
    }

    @Test
    public void respondScheduleTest() {
        // accept schedule
        when(mockScheduleViewRespondViewModel.getCurrentUserNewResponse()).thenReturn(new UserScheduleResponse(Response.ACCEPT, Timestamp.now()));
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.ACCEPT, Timestamp.now()));
        onView(withId(R.id.button_accept)).perform(click());
        verify(mockScheduleViewRespondViewModel, times(1)).isOneMinuteAfterLastResponse();
        verify(mockScheduleViewRespondViewModel, times(1)).respondSchedule(testSchedule.getUid(), Response.ACCEPT);
        verify(mockScheduleViewRespondViewModel, times(1)).notifySupervisorScheduleResponse();
        onView(withId(R.id.linear_layout_response)).check(matches(isDisplayed()));
        onView(withId(R.id.button_accept)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_reject)).check(matches(isDisplayed()));

        // reject schedule
        when(mockScheduleViewRespondViewModel.getCurrentUserNewResponse()).thenReturn(new UserScheduleResponse(Response.REJECT, Timestamp.now()));
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.REJECT, Timestamp.now()));
        onView(withId(R.id.button_reject)).perform(click());
        verify(mockScheduleViewRespondViewModel, times(2)).notifySupervisorScheduleResponse();
        onView(withId(R.id.linear_layout_response)).check(matches(isDisplayed()));
        onView(withId(R.id.button_accept)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reject)).check(matches(not(isDisplayed())));
    }
}