package com.example.bigowlapp.activity;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.bigowlapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

public class InvitationConfirmationActivityTest {

    @Rule
    public ActivityScenarioRule<InvitationConfirmationActivity> invitationConfirmationActivityTestRule = new ActivityScenarioRule<InvitationConfirmationActivity>(InvitationConfirmationActivity.class);
    private InvitationConfirmationActivity invitationConfirmActivity = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(HomePageActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        invitationConfirmationActivityTestRule.getScenario().onActivity(activity -> {
            invitationConfirmActivity = activity;
        });
    }

    @Test
    public void launchHomePageActivityOnButtonClick() {
        assertNotNull(invitationConfirmActivity.findViewById(R.id.returnHome));
        assertNotNull(invitationConfirmActivity.findViewById(R.id.confirmationMessage));
        onView(withId(R.id.returnHome)).perform(click());
        Activity homePageActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(homePageActivity);
        homePageActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        invitationConfirmActivity = null;
    }

}