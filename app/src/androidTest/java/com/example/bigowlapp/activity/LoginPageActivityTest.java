package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.LogInViewModel;
import com.google.android.gms.tasks.Tasks;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginPageActivityTest {
    @Rule
    public ActivityScenarioRule<LoginPageActivity> activityRule = new ActivityScenarioRule<>(LoginPageActivity.class);

    @Mock
    private LogInViewModel mockLogInViewModel;

    private String userEmail = "john.doe@email.com";
    private String userPassword = "abc123";
    private String fakeUserEmail = "testuser1@email.com";
    LoginPageActivity loginPageActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setLogInViewModel(mockLogInViewModel);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    //No user and No password
    public void noUserNoPasswordLoginUserScreenTest() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextTextPassword)).perform(typeText(""), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button))
                .check(matches(withText("Sign In"))).perform(click());

        onView(withId(R.id.editTextTextEmailAddress)).check(matches(hasErrorText("Please enter a valid email")));
    }

    @Test
    //valid user and no password
    public void validUserNoPasswordLoginUserScreenTest() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText(fakeUserEmail), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextTextPassword)).perform(typeText(""), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button))
                .check(matches(withText("Sign In"))).perform(click());

        onView(withId(R.id.editTextTextPassword)).check(matches(hasErrorText("Please enter your password")));
    }

    @Test
    //valid user and wrong password
    public void validUserWrongPasswordLoginUserScreenTest() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText(fakeUserEmail), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextTextPassword)).perform(typeText("abc123"), ViewActions.closeSoftKeyboard());

        Mockito.when(mockLogInViewModel.logInUser(fakeUserEmail, "abc123")).thenReturn(Tasks.forException(new Exception("Failed to authenticate")));

        onView(withId(R.id.button))
                .check(matches(withText("Sign In"))).perform(click());

        onView(withId(R.id.editTextTextEmailAddress)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPassword)).check(matches(isDisplayed()));
    }

    @Test
    //valid user and valid password
    public void validUserValidPasswordLoginUserScreenTest() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText(userEmail), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextTextPassword)).perform(typeText(userPassword), ViewActions.closeSoftKeyboard());

        Mockito.when(mockLogInViewModel.logInUser(userEmail, userPassword)).thenReturn(Tasks.forResult(null));

        onView(withId(R.id.button))
                .check(matches(withText("Sign In"))).perform(click());

        Mockito.verify(mockLogInViewModel).logInUser(userEmail, userPassword);
    }
}