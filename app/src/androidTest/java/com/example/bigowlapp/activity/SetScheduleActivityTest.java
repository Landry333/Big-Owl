package com.example.bigowlapp.activity;

import androidx.fragment.app.Fragment;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.fragments.ScheduleFormFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetScheduleActivityTest {

    @Rule
    public ActivityScenarioRule<SetScheduleActivity> activityRule = new ActivityScenarioRule<>(SetScheduleActivity.class);

    private SetScheduleActivity setScheduleActivity;

    @Before
    public void setUp() throws Exception {
        activityRule.getScenario().onActivity(activity -> {
            setScheduleActivity = activity;
        });
    }

    @Test
    public void shouldGoToScheduleFormFragmentOnExecution() {
        List<Fragment> fragmentsOnPage = setScheduleActivity.getSupportFragmentManager().getFragments();
        // check that only one page is loaded
        assertEquals(1, fragmentsOnPage.size());
        Fragment currentFragment = fragmentsOnPage.get(fragmentsOnPage.size() - 1);
        // check that the activity starts on ScheduleFormFragment page
        assertEquals(ScheduleFormFragment.class, currentFragment.getClass());
    }

}