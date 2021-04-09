package com.example.bigowlapp.activity;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.bigowlapp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchContactsToSuperviseTest {

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_CONTACTS);

    private ActivityScenario<SearchContactsToSupervise> scenario;

    @Before
    public void setUp() throws Exception {
        scenario = ActivityScenario.launch(SearchContactsToSupervise.class);
        scenario.onActivity(activity -> {
            // do stuff with activity where needed
        });
    }

    @Test
    public void searchForContactsSearchBarWorking() {
        onView(withId(R.id.search_users)).perform(replaceText("Search String"));
        onView(withId(R.id.search_users)).check(matches(withText("Search String")));
        onView(withId(R.id.search_users)).perform(replaceText(""));
        onView(withId(R.id.search_users)).check(matches(withText("")));
    }
}