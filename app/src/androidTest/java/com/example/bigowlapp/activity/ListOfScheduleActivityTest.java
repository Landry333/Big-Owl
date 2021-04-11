package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.SystemClock;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.view_model.ScheduleListViewModel;
import com.example.bigowlapp.view_model.ScheduleListViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.sql.Time;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private User testGroupSupervisor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        testArrivingIntent = new Intent(ApplicationProvider.getApplicationContext(), ListOfScheduleActivity.class);
        testArrivingIntent.putExtra("groupID", "testArrivingIntentGroupUId");
        testArrivingIntent.putExtra("groupName", "testArrivingIntentGroupName");
        testArrivingIntent.putExtra("supervisorName", "testArrivingIntentSupervisorName");
        testArrivingIntent.putExtra("isUserTheGroupSupervisor", false);
        testArrivingIntent.putExtra("supervisorId", "testArrivingIntentSupervisorId");


        List<String> testMemberGroupList = new ArrayList<>();
        testMemberGroupList.add("testIntentGroupUId");
        testGroupSupervisor = new User(
                "testArrivingIntentSupervisorId",
                "first",
                "name001",
                "+123001",
                "group001@mail.com",
                null,
                testMemberGroupList
        );
        LiveDataWithStatus<User> groupSupervisorData = new LiveDataWithStatus<>(testGroupSupervisor);

        when(ScheduleListViewModel.isCurrentUserSet()).thenReturn(true);

        // setup user Schedule list
        testUserScheduleList = Collections.singletonList(Schedule.getPrototypeSchedule());
        testScheduleListData = new LiveDataWithStatus<>(testUserScheduleList);
        when(ScheduleListViewModel.getScheduleList(eq(false), any())).thenReturn(testScheduleListData);

        activityScenario = ActivityScenario.launch(testArrivingIntent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            currentActivity = activity;
            activity.setScheduleListViewModel(ScheduleListViewModel);
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void noScheduleTest() {
        testScheduleListData.postValue(null);
        onView(withText("No schedule found!")).check(matches(isDisplayed()));
    }

    @Test
    public void viewScheduleTest() {
        testScheduleListData.postValue(getScheduleList());
        onView(withText("sched3")).check(matches(isDisplayed()));
    }

    @Test
    @Ignore
    public void clickOnScheduleTestAsSupervisor() {
        Intent getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertNull(getIntentToSchedule);
        Schedule randomTestSchedule = testUserScheduleList.get((int) ((Math.random() * 3)));
        onView(allOf(withId(R.id.text_view_schedule_title), withText(randomTestSchedule.getTitle()))).perform(click());

        Intent testIntent = new Intent(currentActivity, SupervisedGroupPageActivity.class);
        testIntent.putExtra("scheduleUid", randomTestSchedule.getUid());
        testIntent.putExtra("supervisorId", randomTestSchedule.getGroupSupervisorUid());

        getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertEquals(randomTestSchedule.getUid(), getIntentToSchedule.getStringExtra("scheduleUid"));
        assertEquals(randomTestSchedule.getGroupSupervisorUid(), getIntentToSchedule.getStringExtra("supervisorId"));
    }

    @Test
    @Ignore
    public void clickOnScheduleTestAsMonitoredUser() {
        Intent getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertNull(getIntentToSchedule);
        Schedule randomTestSchedule = testUserScheduleList.get((int) ((Math.random() * 3)));
        onView(allOf(withId(R.id.text_view_schedule_title), withText(randomTestSchedule.getTitle()))).perform(click());

        Intent testIntent = new Intent(currentActivity, SupervisedGroupPageActivity.class);
        testIntent.putExtra("scheduleUid", randomTestSchedule.getUid());
        testIntent.putExtra("supervisorId", randomTestSchedule.getGroupSupervisorUid());
        testIntent.putExtra("groupName", "groupName");
        testIntent.putExtra("supervisorName", randomTestSchedule.getGroupSupervisorUid());

        getIntentToSchedule = currentActivity.getIntentToScheduleForTest();
        assertEquals(randomTestSchedule.getUid(), getIntentToSchedule.getStringExtra("scheduleUid"));
        assertEquals(randomTestSchedule.getGroupSupervisorUid(), getIntentToSchedule.getStringExtra("supervisorId"));
    }


    private List<Schedule> getScheduleList() {
        List<Schedule> scheduleList = new ArrayList<>();

        for (int i = 0; i < 10 ; i++) {
            Schedule newSchedule = new Schedule();
            newSchedule.setStartTime(Timestamp.now());
            newSchedule.setTitle("sched"+i);
            scheduleList.add(newSchedule);
        }
        return scheduleList;
    }
}