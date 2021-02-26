package com.example.bigowlapp.activity;

import android.content.ContextWrapper;
import android.content.Intent;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SupervisedGroupPageActivityTest {
    private SupervisedGroupPageActivity currentActivity;
    private Intent testArrivingIntent;
    private ActivityScenario<SupervisedGroupPageActivity> activityScenario;

    @Before
    public void setUp() {
        testArrivingIntent = new Intent(ApplicationProvider.getApplicationContext(), SupervisedGroupPageActivity.class);
        testArrivingIntent.putExtra("groupID", "testIntentGroupUId");
        testArrivingIntent.putExtra("groupName", "testIntentGroupName");
        testArrivingIntent.putExtra("supervisorName", "testIntentSupervisorName");

        activityScenario = ActivityScenario.launch(testArrivingIntent);

        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            currentActivity = activity;
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void test1() {
        onView(withId(R.id.group_name_view)).check(matches(withText("testIntentGroupName")));
        onView(withId(R.id.btn_schedule_list)).check(matches(isDisplayed())).perform(click());

        Intent targetIntentToScheduleList = currentActivity.getIntentToScheduleList();
        assertEquals(testArrivingIntent.getStringExtra("groupID"), targetIntentToScheduleList.getStringExtra("groupID"));
        assertEquals(testArrivingIntent.getStringExtra("groupName"), targetIntentToScheduleList.getStringExtra("groupName"));
        assertEquals(testArrivingIntent.getStringExtra("supervisorName"), targetIntentToScheduleList.getStringExtra("supervisorName"));
    }
}