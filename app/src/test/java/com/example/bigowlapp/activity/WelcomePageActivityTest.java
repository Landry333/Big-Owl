package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.N_MR1)
public class WelcomePageActivityTest {
    @Test
    public void welcomePageGoesToLoginPageActivityTest() {
        ActivityController<WelcomePageActivity> welcomeActivityController =
                Robolectric.buildActivity(WelcomePageActivity.class)
                        .create()
                        .start();
        // Takes into account the delay
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        WelcomePageActivity welcomeActivity = welcomeActivityController.get();
        Intent expectedIntent = new Intent(welcomeActivity, LoginPageActivity.class);
        assertTrue(expectedIntent.filterEquals(shadowOf(welcomeActivity).getNextStartedActivity()));
    }
}
