package com.example.bigowlapp.activity;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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

import org.junit.After;
import org.junit.Before;
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

    private ActivityScenario<ScheduleViewRespondActivity> activityScenario;

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

    private Schedule testSchedule;
    private MutableLiveData<Schedule> testScheduleData;
    private User testCurrentUser;

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
        testCurrentUser = new User(
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
        testSchedule.setGroupSupervisorUId(testSupervisor.getUId());
        testSchedule.setStartTime(Timestamp.now());
        testSchedule.setEndTime(Timestamp.now());
        testSchedule.setLocation(new GeoPoint(0, 0));
        testSchedule.setGroupSupervisorUId(testSupervisor.getUId());
        testSchedule.setUserScheduleResponseMap(testScheduleMembersMap);
        testSchedule.setTitle("testSchedule001");
        testSchedule.setEvent("testEvent001");
        testSchedule.setMemberList(testScheduleMemberList);

        testScheduleData = new MutableLiveData<>(testSchedule);
        testScheduleData.postValue(testSchedule);

        when(testFirebaseCurrentUser.getUid()).thenReturn(testCurrentUser.getUId());
        when(mockAuthRepository.getCurrentUser()).thenReturn(testFirebaseCurrentUser);
        when(mockAuthRepository.getCurrentUser().getUid()).thenReturn(testCurrentUser.getUId());
        when(mockNotificationRepository.addDocument(any())).thenReturn(null);

        doAnswer(a -> {
            testScheduleMembersMap.put(testCurrentUser.getUId(), mockScheduleViewRespondViewModel.getCurrentUserNewResponse());
            testSchedule.getUserScheduleResponseMap().put(testCurrentUser.getUId(), mockScheduleViewRespondViewModel.getCurrentUserNewResponse());
            testScheduleData.postValue(testSchedule);
            return null;
        }).when(mockScheduleViewRespondViewModel).respondSchedule(any(), any());

        // TODO: NOTE: probably not needed
        when(mockScheduleRepository.getDocumentByUId(testSchedule.getuId(), Schedule.class)).thenReturn(testScheduleData);

        when(mockScheduleViewRespondViewModel.isCurrentUserSet()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.isCurrentUserInSchedule()).thenReturn(true);
        when(mockScheduleViewRespondViewModel.getCurrentScheduleData(anyString())).thenReturn(testScheduleData);
        when(mockScheduleViewRespondViewModel.isOneMinuteAfterLastResponse()).thenReturn(true);

        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.NEUTRAL, null));

        // Setup the activity to run with its intent data
        Intent testIntent = new Intent(ApplicationProvider.getApplicationContext(), ScheduleViewRespondActivity.class);
        testIntent.putExtra("scheduleUId", testSchedule.getuId());
        testIntent.putExtra("groupName", "test group");
        testIntent.putExtra("supervisorName", testSupervisor.getFullName());

        activityScenario = ActivityScenario.launch(testIntent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            activity.setScheduleViewRespondViewModel(mockScheduleViewRespondViewModel);
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void scheduleWithNoRespondedBeforeTest() {
        // a schedule with no response will be neutral by default
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.NEUTRAL, null));

        verify(mockScheduleViewRespondViewModel, times(1)).getCurrentScheduleData(testSchedule.getuId());
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
        // click accept button
        when(mockScheduleViewRespondViewModel.getCurrentUserNewResponse()).thenReturn(new UserScheduleResponse(Response.ACCEPT, Timestamp.now()));
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.ACCEPT, Timestamp.now()));
        onView(withId(R.id.button_accept)).perform(click());

        verify(mockScheduleViewRespondViewModel, times(1)).isOneMinuteAfterLastResponse();

        // verify runs then set the value
        verify(mockScheduleViewRespondViewModel, times(1)).respondSchedule(testSchedule.getuId(), Response.ACCEPT);


        // TODO: Note: This does not work because you are using a mock (which doesn't run the internal code of the methods within the mock)
        //       and isn't necessary anyways because this code runs within the viewModel
        //
        //       You want to verify that things that you expect the activity to interface with (public methods/attributes), not internal methods in the viewmodel
        //       This is because your goal is not to test the viewModel, but the activity. The viewmodel is just a black box, and you only care about inputting to it and its outputs
//        verify(mockScheduleRepository, times(1)).updateScheduleMemberResponse(
//                testSchedule.getuId(),
//                mockAuthRepository.getCurrentUser().getUid(),
//                mockScheduleViewRespondViewModel.getCurrentUserNewResponse()
//        );
//
        verify(mockScheduleViewRespondViewModel, times(1)).notifySupervisorScheduleResponse();
        onView(withId(R.id.linear_layout_response)).check(matches(isDisplayed()));
        onView(withId(R.id.button_accept)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_reject)).check(matches(isDisplayed()));

        // click reject button
        when(mockScheduleViewRespondViewModel.getCurrentUserNewResponse()).thenReturn(new UserScheduleResponse(Response.REJECT, Timestamp.now()));
        when(mockScheduleViewRespondViewModel.getUserScheduleResponse()).thenReturn(new UserScheduleResponse(Response.REJECT, Timestamp.now()));
        onView(withId(R.id.button_reject)).perform(click());

        // TODO: Note: same as above statement on line 200
//        verify(mockScheduleRepository, times(1)).updateScheduleMemberResponse(
//                testSchedule.getuId(),
//                mockAuthRepository.getCurrentUser().getUid(),
//                mockScheduleViewRespondViewModel.getCurrentUserNewResponse()
//        );
        verify(mockScheduleViewRespondViewModel, times(2)).notifySupervisorScheduleResponse();
        onView(withId(R.id.linear_layout_response)).check(matches(isDisplayed()));
        onView(withId(R.id.button_accept)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reject)).check(matches(not(isDisplayed())));
    }

    @After
    public void tearDown() throws Exception {
        activityScenario.close();
    }
}