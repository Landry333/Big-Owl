package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MonitoringGroupPageActivityTest {

    @Rule
    public ActivityScenarioRule<MonitoringGroupPageActivity> activityRule = new ActivityScenarioRule<>(MonitoringGroupPageActivity.class);

    @Mock
    MonitoringGroupPageViewModel mockViewModel;

    MutableLiveData<Group> testGroupData;
    MutableLiveData<List<User>> testUserListData;
    MonitoringGroupPageActivity currentActivity;

    Group testMonitoringGroup;
    List<User> testUserList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUserList = new ArrayList<>();
        List<String> supervisedUsersIDs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            User testUser = new User(
                    "00".concat(String.valueOf(i)),
                    "Tester",
                    "#".concat(String.valueOf(i)),
                    null,
                    null,
                    null,
                    null);
            testUserList.add(testUser);
            supervisedUsersIDs.add(String.valueOf(i));
        }
        testMonitoringGroup = new Group("abc123", "It's a group for testing", "0", supervisedUsersIDs);

        testGroupData = new MutableLiveData<>();
        testUserListData = new MutableLiveData<>();

        when(mockViewModel.isCurrentUserSet()).thenReturn(true);
        when(mockViewModel.getGroup()).thenReturn(testGroupData);
        when(mockViewModel.getUsersFromGroup(any(Group.class))).thenReturn(testUserListData);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            currentActivity = activity;
            activity.setmGroupPageViewModel(mockViewModel);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void removeOneUserFromMonitoringGroupTest() {
        testGroupData.postValue(testMonitoringGroup);
        testUserListData.postValue(testUserList);

        SystemClock.sleep(500);

        assertEquals(testMonitoringGroup, currentActivity.getmGroupPageViewModel().getGroup().getValue());
        assertEquals(testUserList,
                currentActivity
                        .getmGroupPageViewModel()
                        .getUsersFromGroup(currentActivity.getmGroupPageViewModel().getGroup().getValue())
                        .getValue());

        int randomIndex = (int) (Math.random() * testUserList.size());
        onData(anything())
                .inAdapterView(withId(R.id.list_view_monitoring_users))
                .atPosition(randomIndex)
                .check(matches(withText(testUserList.get(randomIndex).getFullName())))
                .perform(longClick());

        int testUserListAfterUserRemovedSize = testUserList.size() - 1;

        onView(allOf(withText("Remove"), isDisplayed()))
                .perform(click());

        assertEquals(testUserListAfterUserRemovedSize, testUserList.size());
    }

    public static Matcher<View> withListSize(final int size) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(final View view) {
                return ((ListView) view).getCount() == size;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("ListView should have " + size + " items");
            }
        };
    }
}
