package com.example.bigowlapp.activity;

import android.Manifest;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.SmsSender;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SendSmsInvitationActivityTest {

    public static final String STRING_TO_BE_TYPED = "80769";

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.SEND_SMS);

    @Rule
    public ActivityScenarioRule<SendSmsInvitationActivity> activityScenarioRule
            = new ActivityScenarioRule<>(SendSmsInvitationActivity.class);

    @Mock
    private SmsSender mockSmsSender;

    @Before
    public void setUp() throws Exception {
        // Added because we didn't add "@RunWith(Mockito)"
        initMocks(this);

        activityScenarioRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityScenarioRule.getScenario().onActivity(activity -> activity.setSmsSender(mockSmsSender));
        activityScenarioRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void launchTextSmsOnButtonClick() {
        onView(withId(R.id.number)).perform(typeText(STRING_TO_BE_TYPED),
                closeSoftKeyboard());
        onView(withId(R.id.send)).perform(click());
        verify(mockSmsSender).sendSMS(eq(STRING_TO_BE_TYPED), anyString());
    }
}
