package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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

    AutoCloseable autoCloseable;
    MutableLiveData<Group> testGroupData;
    MutableLiveData<List<User>> testUserListData;
    MonitoringGroupPageActivity currentActivity;

    Group testMonitoringGroup;
    List<User> testUserList;

    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        testUserList = new ArrayList<>();
        List<String> supervisedUsersIDs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            User testUser = new User(
                    "00".concat(String.valueOf(i)),
                    "Tester",
                    "#".concat(String.valueOf(i)),
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

        SystemClock.sleep(3000);

        assertEquals(testMonitoringGroup, currentActivity.getmGroupPageViewModel().getGroup().getValue());
        assertEquals(testUserList,
                currentActivity
                        .getmGroupPageViewModel()
                        .getUsersFromGroup(currentActivity.getmGroupPageViewModel().getGroup().getValue())
                        .getValue());

        SystemClock.sleep(3000);

        int randomIndex = (int) (Math.random() * testUserList.size());
        onData(anything())
                .inAdapterView(withId(R.id.users_list_view))
                .atPosition(randomIndex)
                .check(matches(withText(testUserList.get(randomIndex).getFullName())))
                .perform(longClick());

        int testUserListAfterUserRemovedSize = testUserList.size() - 1;

        SystemClock.sleep(3000);

        onView(allOf(withText("Remove"), isDisplayed()))
                .perform(click());

        SystemClock.sleep(3000);

        assertEquals(testUserListAfterUserRemovedSize, testUserList.size());

        SystemClock.sleep(3000);
    }

    @After
    public void testDone() throws Exception {
        autoCloseable.close();
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
