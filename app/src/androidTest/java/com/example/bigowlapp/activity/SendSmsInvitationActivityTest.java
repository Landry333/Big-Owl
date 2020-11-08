package com.example.bigowlapp.activity;

import android.app.Activity;

        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        import androidx.test.espresso.action.ViewActions;
        import androidx.test.espresso.matcher.ViewMatchers;
        import androidx.test.ext.junit.rules.ActivityScenarioRule;
        import androidx.test.ext.junit.runners.AndroidJUnit4;
        import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;

import static androidx.test.espresso.Espresso.onView;
        import static androidx.test.espresso.action.ViewActions.click;
        import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
        import static androidx.test.espresso.action.ViewActions.typeText;
        import static androidx.test.espresso.assertion.ViewAssertions.matches;
        import static androidx.test.espresso.matcher.ViewMatchers.withId;
        import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SendSmsInvitationActivityTest {

    public static final String STRING_TO_BE_TYPED = "80769";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes. This is a replacement for {@link androidx.test.rule.ActivityTestRule}.
     */
    @Rule public ActivityScenarioRule<SendSmsInvitationActivity> activityScenarioRule
            = new ActivityScenarioRule<>(SendSmsInvitationActivity.class);

    @Test
    public void changeText_newActivity() {
        // Type text and then press the button.
        onView(withId(R.id.number)).perform(typeText(STRING_TO_BE_TYPED),
                closeSoftKeyboard());
        onView(withId(R.id.send)).perform(click());

    }
}
