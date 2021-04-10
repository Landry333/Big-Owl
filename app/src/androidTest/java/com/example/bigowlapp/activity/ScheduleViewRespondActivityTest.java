package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.utils.GeoLocationFormatter;
import com.example.bigowlapp.viewModel.ScheduleViewRespondViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduleViewRespondActivityTest {

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Mock
    private ScheduleViewRespondViewModel mockScheduleViewRespondViewModel;

    @Mock
    private NotificationRepository mockNotificationRepository;

    @Mock
    private FirebaseUser testFirebaseCurrentUser;

    @Mock
    private GeoLocationFormatter mockGeoLocationFormatter;

    private Schedule testSchedule;
    private final Timestamp timeNow = Timestamp.now();
    private final String CONCORDIA_ADDRESS = "1571 Rue Mackay, Montréal, QC H3G 2H6, Canada";
    private final static int ONE_HOUR_SECONDS = 3600;
    private MutableLiveData<Schedule> testScheduleData;
    private User testCurrentUser;
    private Map<String, UserScheduleResponse> testScheduleMembersMap;

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
        testCurrentUser = new User(
                "testCurrentUser001",
                "test",
                "currentUser",
                "+1111111111",
                "testCurrentUser@mail.com",
                null,
                null);

        testScheduleMembersMap = new HashMap<>();
        testScheduleMembersMap.put(testCurrentUser.getUid(),
                new UserScheduleResponse(Response.NEUTRAL, null));
        testSchedule = new Schedule();
        testSchedule.setUid("schedule001");
        testSchedule.setTitle("testSchedule001");
        testSchedule.setEvent("testEvent001");
        testSchedule.setGroupUid("testGroup001");
        testSchedule.setGroupSupervisorUid(testSupervisor.getUid());
        testSchedule.setStartTime(new Timestamp(timeNow.getSeconds() + ONE_HOUR_SECONDS, 0));
        testSchedule.setEndTime(new Timestamp(timeNow.getSeconds() + 2 * ONE_HOUR_SECONDS, 0));
        testSchedule.setLocation(new GeoPoint(45.49661075, -73.57853574999999));
        testSchedule.setUserScheduleResponseMap(testScheduleMembersMap);
        Intent testIntent = new Intent(ApplicationProvider.getApplicationContext(), ScheduleViewRespondActivity.class);
        testIntent.putExtra("scheduleUid", testSchedule.getUid());
        testIntent.putExtra("groupName", "test group");
        testIntent.putExtra("supervisorName", testSupervisor.getFullName());
        testScheduleData = new MutableLiveData<>();
        testScheduleData.postValue(testSchedule);

        when(testFirebaseCurrentUser.getUid()).thenReturn(testCurrentUser.getUid());
        when(mockNotificationRepository.addDocument(any())).thenReturn(null);
        when(mockScheduleViewRespondViewModel.isCurrentUserSet()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.getCurrentUserUid()).thenReturn(testCurrentUser.getUid());
        when(mockScheduleViewRespondViewModel.getCurrentScheduleData(testSchedule.getUid())).thenReturn(testScheduleData);
        when(mockScheduleViewRespondViewModel.isCurrentUserInSchedule()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.isOneMinuteAfterLastResponse()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(testSchedule.getUserScheduleResponseMap().get(testCurrentUser.getUid()));
        doNothing().when(mockScheduleViewRespondViewModel).notifySupervisorScheduleResponse();
        doAnswer(a -> {
            testSchedule.getUserScheduleResponseMap().put(
                    testCurrentUser.getUid(), mockScheduleViewRespondViewModel.getCurrentUserNewResponse());
            mockScheduleViewRespondViewModel.notifySupervisorScheduleResponse();

            testScheduleData.postValue(testSchedule);
            return null;
        }).when(mockScheduleViewRespondViewModel).respondSchedule(any(), any());
        when(mockGeoLocationFormatter.formatLocation(any(Context.class), any(GeoPoint.class))).thenReturn(CONCORDIA_ADDRESS);

        ActivityScenario<ScheduleViewRespondActivity> activityScenario = ActivityScenario.launch(testIntent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            activity.setScheduleViewRespondViewModel(mockScheduleViewRespondViewModel);
            activity.setGeoLocationFormatter(mockGeoLocationFormatter);
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void scheduleWithNoRespondedBeforeTest() {
        verify(mockScheduleViewRespondViewModel, times(1)).getCurrentScheduleData(testSchedule.getUid());
        verify(mockScheduleViewRespondViewModel, times(1)).isCurrentUserInSchedule();
        onView(withId(R.id.linear_layout_schedule_view)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_title)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_group_uid)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_group_supervisor_name)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_start_time)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_end_time)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_location)).check(matches(isDisplayed()));
        onView(withId(R.id.view_divider_below_schedule)).check(matches(isDisplayed()));
        onView(withId(R.id.linear_layout_system_response)).check(matches(not(isDisplayed())));
        onView(withId(R.id.line_below_system_response)).check(matches(not(isDisplayed())));
        verify(mockScheduleViewRespondViewModel, times(1)).getUserScheduleResponse();
        onView(withId(R.id.button_accept)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reject)).check(matches(isDisplayed()));
    }

    @Test
    public void respondScheduledScheduleTest() {
        // accept schedule
        when(mockScheduleViewRespondViewModel.getCurrentUserNewResponse()).thenReturn(new UserScheduleResponse(Response.ACCEPT, timeNow));
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.ACCEPT, timeNow));
        onView(withId(R.id.button_accept)).perform(click()).check(matches(isDisplayed()));;
        verify(mockScheduleViewRespondViewModel, times(1)).isOneMinuteAfterLastResponse();
        verify(mockScheduleViewRespondViewModel, times(1)).respondSchedule(testSchedule.getUid(), Response.ACCEPT);
        verify(mockScheduleViewRespondViewModel, times(1)).notifySupervisorScheduleResponse();

        when(mockScheduleViewRespondViewModel.getCurrentUserNewResponse()).thenReturn(new UserScheduleResponse(Response.REJECT, timeNow));
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.REJECT, timeNow));
        onView(withId(R.id.button_reject)).perform(click()).check(matches(isDisplayed()));;
        verify(mockScheduleViewRespondViewModel, times(2)).notifySupervisorScheduleResponse();
    }

    @Test
    public void completedScheduleNoResponseTest() {
        testSchedule.setStartTime(new Timestamp(timeNow.getSeconds() - 3 * ONE_HOUR_SECONDS, 0));
        testSchedule.setEndTime(new Timestamp(timeNow.getSeconds() - 2 * ONE_HOUR_SECONDS, 0));
        testScheduleData.postValue(testSchedule);

        onView(allOf(withId(R.id.text_view_schedule_member_attendance), withText("NO RESPONSE"))).check(matches(isDisplayed()));
    }
}