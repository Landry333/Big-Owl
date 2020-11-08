package com.example.bigowlapp.activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SendSmsInvitationActivityTest {

    public static final String STRING_TO_BE_TYPED = "80769";

    @Rule
    public ActivityScenarioRule<SendSmsInvitationActivity> activityScenarioRule
            = new ActivityScenarioRule<>(SendSmsInvitationActivity.class);

    @Test
    public void launchTextSmsOnButtonClick() {

        onView(withId(R.id.number)).perform(typeText(STRING_TO_BE_TYPED),
                closeSoftKeyboard());
        onView(withId(R.id.send)).perform(click());

    }
}
