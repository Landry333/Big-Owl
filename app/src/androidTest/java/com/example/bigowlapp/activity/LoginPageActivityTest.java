package com.example.bigowlapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.viewModel.LogInViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginPageActivityTest {
    @Rule
    public ActivityScenarioRule<LoginPageActivity> activityRule = new ActivityScenarioRule<>(LoginPageActivity.class);

    @Mock
    private LogInViewModel mockLogInViewModel;

    private String userEmail;
    private String userPassword;
    LoginPageActivity loginPageActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userEmail = "testuser1@email.com";
        userPassword = "testuser1";

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            loginPageActivity = activity;
            activity.setHomePageViewModel(mockLogInViewModel);
        });

        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    //valid user and valid password
    public void ValidUserValidPasswordLoginUserScreenTest(){

    }

    @Test
    //valid user and wrong password
    public void ValidUserWrongPasswordLoginUserScreenTest(){

    }

}