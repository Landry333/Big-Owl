package com.example.bigowlapp.activity;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.view_model.ScheduleListViewModel;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListOfScheduleActivityTest {
    @Rule
    public ActivityScenarioRule<ListOfScheduleActivity> activityRule = new ActivityScenarioRule<>(ListOfScheduleActivity.class);
    @Mock
    private ScheduleListViewModel ScheduleListViewModel;
    private Intent testArrivingIntent;
    private List<Schedule> testUserScheduleList;
    private LiveDataWithStatus<List<Schedule>> testScheduleListData;
    private ListOfScheduleActivity currentActivity;
    private ActivityScenario<ListOfScheduleActivity> activityScenario;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        testArrivingIntent = new Intent(ApplicationProvider.getApplicationContext(), ListOfScheduleActivity.class);
        testArrivingIntent.putExtra("groupID", "testArrivingIntentGroupUId");
        testArrivingIntent.putExtra("groupName", "testArrivingIntentGroupName");
        testArrivingIntent.putExtra("supervisorName", "testArrivingIntentSupervisorName");
        testArrivingIntent.putExtra("isUserTheGroupSupervisor", false);
        testArrivingIntent.putExtra("supervisorId", "testArrivingIntentSupervisorId");

        when(ScheduleListViewModel.isCurrentUserSet()).thenReturn(true);

        // setup user Schedule list
        testUserScheduleList = Collections.singletonList(Schedule.getPrototypeSchedule());
        testScheduleListData = new LiveDataWithStatus<>(testUserScheduleList);
        when(ScheduleListViewModel.getScheduleList(eq(false), any())).thenReturn(testScheduleListData);
        when(ScheduleListViewModel.getScheduleList(eq(true), any())).thenReturn(testScheduleListData);

        startActivityScenario(testArrivingIntent);
    }

    @Test
    public void noScheduleTest() {
        startActivityScenario(testArrivingIntent);
        testScheduleListData.postValue(null);
        onView(withText("No schedule found!")).check(matches(isDisplayed()));
    }

    @Test
    public void viewScheduleTest() {
        startActivityScenario(testArrivingIntent);
        testScheduleListData.postValue(getScheduleList());
        onView(withText("sched3")).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnScheduleTestAsSupervisor() {
        testArrivingIntent.putExtra("isUserTheGroupSupervisor", true);
        startActivityScenario(testArrivingIntent);
        testScheduleListData.postValue(getScheduleList());
        Intent getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertNull(getIntentToSchedule);
        Schedule randomTestSchedule = getScheduleList().get(1);
        onView(allOf(withId(R.id.text_view_schedule_title), withText(randomTestSchedule.getTitle()))).perform(click());
        Intent testIntent = new Intent(currentActivity, ScheduleReportActivity.class);
        testIntent.putExtra("scheduleUid", randomTestSchedule.getUid());
        testIntent.putExtra("supervisorId", randomTestSchedule.getGroupSupervisorUid());

        getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertEquals(randomTestSchedule.getUid(), getIntentToSchedule.getStringExtra("scheduleUid"));
        assertEquals("supervisor1", randomTestSchedule.getGroupSupervisorUid());
    }

    @Test
    public void clickOnScheduleTestAsMonitoredUser() {
        startActivityScenario(testArrivingIntent);
        testScheduleListData.postValue(getScheduleList());
        Intent getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertNull(getIntentToSchedule);
        Schedule randomTestSchedule = getScheduleList().get(2);
        onView(allOf(withId(R.id.text_view_schedule_title), withText(randomTestSchedule.getTitle()))).perform(click());

        Intent testIntent = new Intent(currentActivity, ScheduleViewRespondActivity.class);
        testIntent.putExtra("scheduleUid", randomTestSchedule.getUid());
        testIntent.putExtra("supervisorId", randomTestSchedule.getGroupSupervisorUid());
        testIntent.putExtra("groupName", "groupName");
        testIntent.putExtra("supervisorName", "supervisor");

        getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertEquals(randomTestSchedule.getUid(), getIntentToSchedule.getStringExtra("scheduleUid"));
        assertEquals("supervisor2", randomTestSchedule.getGroupSupervisorUid());
        assertEquals("testArrivingIntentGroupName", getIntentToSchedule.getStringExtra("groupName"));
        assertEquals("testArrivingIntentSupervisorName", getIntentToSchedule.getStringExtra("supervisorName"));
    }

    private List<Schedule> getScheduleList() {
        List<Schedule> scheduleList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Schedule newSchedule = new Schedule();
            newSchedule.setStartTime(Timestamp.now());
            newSchedule.setTitle("sched" + i);
            newSchedule.setGroupSupervisorUid("supervisor" + i);
            scheduleList.add(newSchedule);
        }
        return scheduleList;
    }

    private void startActivityScenario(Intent intent) {
        activityScenario = ActivityScenario.launch(intent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            currentActivity = activity;
            activity.setScheduleListViewModel(ScheduleListViewModel);
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }
}