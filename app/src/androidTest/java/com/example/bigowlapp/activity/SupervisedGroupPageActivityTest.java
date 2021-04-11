package com.example.bigowlapp.activity;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.SupervisedGroupPageViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SupervisedGroupPageActivityTest {
    private SupervisedGroupPageActivity currentActivity;
    private Intent testArrivingIntent;
    private ActivityScenario<SupervisedGroupPageActivity> activityScenario;
    private User testGroupSupervisor;

    @Mock
    private SupervisedGroupPageViewModel supervisedGroupPageViewModel;

    @Before
    public void setUp() {
        initMocks(this);

        testArrivingIntent = new Intent(ApplicationProvider.getApplicationContext(), SupervisedGroupPageActivity.class);
        testArrivingIntent.putExtra("groupID", "testArrivingIntentGroupUId");
        testArrivingIntent.putExtra("groupName", "testArrivingIntentGroupName");
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

        when(supervisedGroupPageViewModel.getSupervisor(testGroupSupervisor.getUid())).thenReturn(groupSupervisorData);

        activityScenario = ActivityScenario.launch(testArrivingIntent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            currentActivity = activity;
            activity.setSupervisedGroupListViewModel(supervisedGroupPageViewModel);
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.group_name_view)).check(matches(withText(testArrivingIntent.getStringExtra("groupName"))));
        onView(withId(R.id.supervisor_name_view)).check(matches(withText(testGroupSupervisor.getFullName())));
    }

    @Test
    public void clickOnScheduleButtonFoesToListOfScheduleTest() {
        onView(withId(R.id.btn_schedule_list)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.list_schedule_page_title)).check(matches(isDisplayed()));
    }
}