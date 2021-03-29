package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;

import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
@Ignore
@RunWith(AndroidJUnit4.class)
@LargeTest
public class WelcomePageActivityTest {

    @Rule
    public ActivityScenarioRule<WelcomePageActivity> mActivityScenario
            = new ActivityScenarioRule<>(WelcomePageActivity.class);

    @Test
    public void shouldStartAtWelcomePageAndGoesToLoginPage() {
        Espresso.onView(isRoot()).perform(waitFor(3000));
        Espresso.onView(withId(R.id.button)).check(matches(isDisplayed()));
    }

    private ViewAction waitFor(final long ms) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SystemClock.sleep(ms);
            }
        };
    }
}
