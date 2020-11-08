package com.example.bigowlapp.activity;

import android.os.SystemClock;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.HomePageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class HomePageActivityTest {

    @Rule
    public ActivityScenarioRule<HomePageActivity> activityRule = new ActivityScenarioRule<>(HomePageActivity.class);

    @Mock
    private HomePageViewModel homePageViewModel;

    private User testUser;
    private MutableLiveData<User> testUserData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new User("abc123", "test user", "abc", "+123", "test@mail.com", null);
        testUserData = new MutableLiveData<>();

        when(homePageViewModel.isCurrentUserSet()).thenReturn(true);
        when(homePageViewModel.getCurrentUserData()).thenReturn(testUserData);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setHomePageViewModel(homePageViewModel);
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

}