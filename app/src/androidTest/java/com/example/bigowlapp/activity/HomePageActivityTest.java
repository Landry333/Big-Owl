package com.example.bigowlapp.activity;

import android.Manifest;
import android.os.SystemClock;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.MemberScheduleAlarmManager;
import com.example.bigowlapp.viewModel.HomePageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageActivityTest {

    @Rule
    public ActivityScenarioRule<HomePageActivity> activityRule = new ActivityScenarioRule<>(HomePageActivity.class);

    @Mock
    private HomePageViewModel homePageViewModel;

    @Mock
    private MemberScheduleAlarmManager memberScheduleAlarmManager;


    private User testUser;
    private LiveDataWithStatus<User> testUserData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new User("abc123", "test user", "abc", "+123", "test@mail.com", null, null);
        testUserData = new LiveDataWithStatus<>();

        when(homePageViewModel.isCurrentUserSet()).thenReturn(true);
        when(homePageViewModel.getCurrentUserData()).thenReturn(testUserData);
        when(homePageViewModel.getCurrentUserUid()).thenReturn(testUser.getUid());

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setHomePageViewModel(homePageViewModel);
            activity.setMemberScheduleAlarmManager(memberScheduleAlarmManager);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }


    @Test
    public void profileViewAtHomePageTest() {
        testUserData.postValue(testUser);

        SystemClock.sleep(5000);

        onView(withText(testUserData.getValue().getFirstName())).check(matches(isDisplayed()));
        onView(withText(testUserData.getValue().getLastName())).check(matches(isDisplayed()));
        onView(withText(testUserData.getValue().getPhoneNumber())).check(matches(isDisplayed()));
        onView(withText(testUserData.getValue().getEmail())).check(matches(isDisplayed()));
    }

    @Test
    public void isAlarmSetForBroadcastReceiver() {
        verify(memberScheduleAlarmManager).setAlarms(testUser.getUid());
    }

}