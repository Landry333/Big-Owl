package com.example.bigowlapp.activity;

import android.content.Intent;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.viewModel.ScheduleViewRespondViewModel;
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

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
public class ScheduleViewRespondActivityTest {

    @Rule
    public ActivityScenarioRule<ScheduleViewRespondActivity> activityRule = new ActivityScenarioRule<>(ScheduleViewRespondActivity.class);

    @Mock
    private ScheduleViewRespondViewModel mockScheduleViewRespondViewModel;

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private NotificationRepository mockNotificationRepository;

    @Mock
    private ScheduleRepository mockScheduleRepository;

    @Mock
    private FirebaseUser testFirebaseCurrentUser;

    Schedule testSchedule;

    @Before
    public void setUp() {
        initMocks(this);

        User testSupervisor = new User(
                "supervisor001",
                "supervisor",
                "001",
                "+1234567890",
                "testSupervisor@mail.com",
                null);
        User testCurrentUser = new User(
                "testCurrentUser001",
                "test",
                "currentUser",
                "+1111111111",
                "testCurrentUser@mail.com",
                null);

        Map<String, UserScheduleResponse> testScheduleMembersMap = new HashMap<>();
        testScheduleMembersMap.put(testCurrentUser.getUId(),
                new UserScheduleResponse(Response.NEUTRAL, null));
        List<String> testScheduleMemberList = new ArrayList<>();
        testScheduleMemberList.add(0, testCurrentUser.getUId());
        testSchedule = new Schedule();
        testSchedule.setuId("schedule001");
        testSchedule.setTitle("testSchedule001");
        testSchedule.setEvent("testEvent001");
        testSchedule.setGroupUId("testGroup001");
        testSchedule.setGroupSupervisorUId(testSupervisor.getUId());
        testSchedule.setStartTime(Timestamp.now());
        testSchedule.setEndTime(new Timestamp(Timestamp.now().getSeconds() + 600000, 0));
        testSchedule.setLocation(new GeoPoint(0, 0));
        testSchedule.setUserScheduleResponseMap(testScheduleMembersMap);
        Intent testIntent = new Intent();
        testIntent.putExtra("scheduleUId", testSchedule.getuId());
        testIntent.putExtra("groupName", "test group");
        testIntent.putExtra("supervisorName", testSupervisor.getFullName());

        MutableLiveData<Schedule> testScheduleData = new MutableLiveData<>(testSchedule);
        testScheduleData.postValue(testSchedule);

        when(testFirebaseCurrentUser.getUid()).thenReturn(testCurrentUser.getUId());
        when(mockAuthRepository.getCurrentUser()).thenReturn(testFirebaseCurrentUser);
        when(mockAuthRepository.getCurrentUser().getUid()).thenReturn(testCurrentUser.getUId());
        when(mockNotificationRepository.addDocument(any())).thenReturn(null);
        when(mockScheduleRepository.updateScheduleMemberResponse(
                anyString(),
                anyString(),
                any(UserScheduleResponse.class)
        )).thenAnswer(a -> {
            testScheduleMembersMap.put(testCurrentUser.getUId(), mockScheduleViewRespondViewModel.getCurrentUserNewResponse());
            testSchedule.setUserScheduleResponseMap(testScheduleMembersMap);
            testScheduleData.postValue(testSchedule);
            return null;
        });
        when(mockScheduleRepository.getDocumentByUId(testSchedule.getuId(), Schedule.class)).thenReturn(testScheduleData);
        when(mockScheduleViewRespondViewModel.isCurrentUserInSchedule()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.getCurrentScheduleData(anyString())).thenReturn(testScheduleData);
        when(mockScheduleViewRespondViewModel.isOneMinuteAfterLastResponse()).thenReturn(true);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setScheduleViewRespondViewModel(mockScheduleViewRespondViewModel);
            activity.setScheduleIntentData(testSchedule.getuId(), "test group", testSupervisor.getFullName());
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void scheduleWithNoRespondedBeforeTest() {
        verify(mockScheduleViewRespondViewModel, times(1)).getCurrentScheduleData(testSchedule.getuId());
        verify(mockScheduleViewRespondViewModel, times(1)).isCurrentUserInSchedule();
        onView(withId(R.id.linear_layout_schedule_view)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_group_uid)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_group_supervisor_name)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_start_time)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_schedule_end_time)).check(matches(isDisplayed()));
        onView(withId(R.id.view_divider_below_schedule)).check(matches(isDisplayed()));
        onView(withId(R.id.linear_layout_response)).check(doesNotExist());
        onView(withId(R.id.line_below_response)).check(doesNotExist());

        verify(mockScheduleViewRespondViewModel, times(1)).getUserScheduleResponse();
        onView(withId(R.id.button_accept)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reject)).check(matches(isDisplayed()));
    }

    @Test
    public void respondScheduleTest() {
        onView(withId(R.id.button_accept)).perform(click());
        verify(mockScheduleViewRespondViewModel, times(1)).isOneMinuteAfterLastResponse();
        verify(mockScheduleViewRespondViewModel, times(1)).respondSchedule(testSchedule.getuId(), Response.ACCEPT);
        verify(mockScheduleRepository, times(1)).updateScheduleMemberResponse(
                testSchedule.getuId(),
                mockAuthRepository.getCurrentUser().getUid(),
                mockScheduleViewRespondViewModel.getCurrentUserNewResponse()
        );
        verify(mockScheduleViewRespondViewModel, times(1)).notifySupervisorScheduleResponse();
        onView(withId(R.id.linear_layout_response)).check(matches(isDisplayed()));
        onView(withId(R.id.button_accept)).check(doesNotExist());
        onView(withId(R.id.button_reject)).check(matches(isDisplayed()));

        onView(withId(R.id.button_reject)).perform(click());
        verify(mockScheduleRepository, times(1)).updateScheduleMemberResponse(
                testSchedule.getuId(),
                mockAuthRepository.getCurrentUser().getUid(),
                mockScheduleViewRespondViewModel.getCurrentUserNewResponse()
        );
        verify(mockScheduleViewRespondViewModel, times(1)).notifySupervisorScheduleResponse();
        onView(withId(R.id.linear_layout_response)).check(matches(isDisplayed()));
        onView(withId(R.id.button_accept)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reject)).check(doesNotExist());
    }
}