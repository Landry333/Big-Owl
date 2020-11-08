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

import static junit.framework.TestCase.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.N_MR1)
public class WelcomePageActivityTest {

    @Test
    public void MainActivityGoesToWelcomePageTest() {
        ActivityController<MainActivity> mainActivityController =
                Robolectric.buildActivity(MainActivity.class)
                .create()
                .start();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        MainActivity mainActivity = mainActivityController.get();
        Intent expectedIntent = new Intent(mainActivity, WelcomePageActivity.class);
        // Needed to put toString() since even if the object are the same, the test fails
        assertEquals(expectedIntent.toString(),
                shadowOf(mainActivity).getNextStartedActivity().toString());
    }

    @Test
    public void WelcomePageGoesToLoginPageActivityTest() {
        ActivityController<WelcomePageActivity> welcomeActivityController =
                Robolectric.buildActivity(WelcomePageActivity.class)
                        .create()
                        .start();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        WelcomePageActivity welcomeActivity = welcomeActivityController.get();
        Intent expectedIntent = new Intent(welcomeActivity, LoginPageActivity.class);
        // Needed to put toString() since even if the object are the same, the test fails
        assertEquals(expectedIntent.toString(),
                shadowOf(welcomeActivity).getNextStartedActivity().toString());
    }
}
