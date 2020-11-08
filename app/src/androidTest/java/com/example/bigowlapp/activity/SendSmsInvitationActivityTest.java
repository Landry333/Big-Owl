package com.example.bigowlapp.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.widget.EditText;

import androidx.test.rule.ActivityTestRule;

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


public class SendSmsInvitationActivityTest {
    @Rule
    public ActivityTestRule<SendSmsInvitationActivity> sendSmsActivityTestRule = new ActivityTestRule<SendSmsInvitationActivity>(SendSmsInvitationActivity.class);
    private SendSmsInvitationActivity sendSmsActivity = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(InvitationConfirmationActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        sendSmsActivity = sendSmsActivityTestRule.getActivity();
        EditText number = (EditText) sendSmsActivity.findViewById(R.id.number);
        number.setText("87659");
    }

    @Test
    public void launchInvitationConfirmationActivityOnButtonClick() {
        assertNotNull(sendSmsActivity.findViewById(R.id.send));
        assertNotNull(sendSmsActivity.findViewById(R.id.number));
        assertNotNull(sendSmsActivity.findViewById(R.id.note));
        assertNotNull(sendSmsActivity.findViewById(R.id.message));
        onView(withId(R.id.send)).perform(click());
        Activity invitationConfirmationActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 6000);
        assertNotNull(invitationConfirmationActivity);
        invitationConfirmationActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        sendSmsActivity = null;
    }

}
