package com.example.bigowlapp.activity;

import android.Manifest;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.bigowlapp.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WelcomePageActivityTest {

    @Rule
    public final GrantPermissionRule permissionRule = getGrantPermissions();

    @NonNull
    private GrantPermissionRule getGrantPermissions() {
        List<String> permissionsToGrant = new ArrayList<>(Arrays.asList(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            permissionsToGrant.add(Manifest.permission.READ_PHONE_NUMBERS);
        }

        return GrantPermissionRule.grant(permissionsToGrant.toArray(new String[0]));
    }

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
