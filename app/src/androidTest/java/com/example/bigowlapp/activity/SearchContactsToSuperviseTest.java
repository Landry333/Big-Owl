package com.example.bigowlapp.activity;

import androidx.test.core.app.ActivityScenario;

import com.example.bigowlapp.R;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SearchContactsToSuperviseTest {

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