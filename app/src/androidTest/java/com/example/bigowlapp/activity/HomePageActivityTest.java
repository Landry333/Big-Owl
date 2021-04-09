package com.example.bigowlapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.MemberScheduleAlarmManager;
import com.example.bigowlapp.view_model.HomePageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
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

        onView(allOf(withText(testUser.getFirstName()), withId(R.id.user_first_name))).check(matches(isDisplayed()));
        onView(allOf(withText(testUser.getLastName()), withId(R.id.user_last_name))).check(matches(isDisplayed()));
        onView(allOf(withText(testUser.getPhoneNumber()), withId(R.id.user_phone_number))).check(matches(isDisplayed()));
        onView(allOf(withText(testUser.getEmail()), withId(R.id.user_email))).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnLogout() {
        onView(withId(R.id.btn_logout)).check(matches(withText("Log out")));
        closeSoftKeyboard();
        onView(withId(R.id.btn_logout))
                .perform(scrollTo())
                .perform(click());
    }

    @Test
    public void clickOnAddUser() {
        onView(withId(R.id.btn_add_users)).check(matches(withText("add user")));
        onView(withId(R.id.btn_add_users)).perform(click());
    }

    @Test
    public void clickOnMonitoringGroup() {
        onView(withId(R.id.btn_monitoring_group)).check(matches(withText("I supervise")));
        onView(withId(R.id.btn_monitoring_group)).perform(click());
    }

    @Test
    public void clickOnSupervisedGroup() {
        onView(withId(R.id.btn_supervised_group)).check(matches(withText("supervising me")));
        onView(withId(R.id.btn_supervised_group)).perform(click());
    }

    @Test
    public void clickOnOverflowMenu() {
        onView(withId(R.id.action_overflow)).check(matches(isDisplayed()));
        onView(withId(R.id.action_overflow)).perform(click());
    }

    @Test
    public void clickOnEditProfileInOverflowMenu() {
        onView(withId(R.id.action_overflow)).check(matches(isDisplayed()));
        onView(withId(R.id.action_overflow)).perform(click());
        onView(withText("Edit Profile")).check(matches(isDisplayed()));
        onView(withText("Edit Profile")).perform(click());
    }

    @Test
    public void clickOnHomeInOverflowMenu() {
        onView(withId(R.id.action_overflow)).check(matches(isDisplayed()));
        onView(withId(R.id.action_overflow)).perform(click());
        onView(withText(R.string.home)).check(matches(isDisplayed()));
        onView(withText(R.string.home)).perform(click());
    }

    @Test
    public void clickOnRefreshInOverflowMenu() {
        onView(withId(R.id.action_overflow)).check(matches(isDisplayed()));
        onView(withId(R.id.action_overflow)).perform(click());
        onView(withText(R.string.refresh)).check(matches(isDisplayed()));
        onView(withText(R.string.refresh)).perform(click());
    }

    @Test
    public void clickOnLogoutInOverflowMenu() {
        onView(withId(R.id.action_overflow)).check(matches(isDisplayed()));
        onView(withId(R.id.action_overflow)).perform(click());
        onView(withText(R.string.log_out)).check(matches(isDisplayed()));
        onView(withText(R.string.log_out)).perform(click());
    }

    @Test
    public void isAlarmSetForBroadcastReceiver() {
        verify(memberScheduleAlarmManager).setAlarms(testUser.getUid());
    }

}