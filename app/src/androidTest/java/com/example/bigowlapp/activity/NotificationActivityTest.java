package com.example.bigowlapp.activity;

import androidx.fragment.app.Fragment;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.bigowlapp.fragments.NotificationListFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NotificationActivityTest {

    @Rule
    public ActivityScenarioRule<NotificationActivity> activityRule = new ActivityScenarioRule<>(NotificationActivity.class);

    private NotificationActivity notificationActivity;

    @Before
    public void setUp() throws Exception {
        activityRule.getScenario().onActivity(activity -> {
            notificationActivity = activity;
        });
    }

    @Test
    public void shouldGoToScheduleFormFragmentOnExecution() {
        List<Fragment> fragmentsOnPage = notificationActivity.getSupportFragmentManager().getFragments();
        // check that only one page is loaded
        assertEquals(1, fragmentsOnPage.size());
        Fragment currentFragment = fragmentsOnPage.get(fragmentsOnPage.size() - 1);
        assertEquals(NotificationListFragment.class, currentFragment.getClass());
    }

}