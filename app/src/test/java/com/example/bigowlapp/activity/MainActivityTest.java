package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.N_MR1)
public class MainActivityTest {
    @Test
    public void MainActivityGoesToWelcomePageTest() {
        ActivityController<MainActivity> mainActivityController =
                Robolectric.buildActivity(MainActivity.class)
                        .create()
                        .start();
        MainActivity mainActivity = mainActivityController.get();
        Intent expectedIntent = new Intent(mainActivity, WelcomePageActivity.class);
        assertTrue(expectedIntent.filterEquals(shadowOf(mainActivity).getNextStartedActivity()));
    }
}
