package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.widget.ListAdapter;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
                    "tester",
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
        testGroupData.postValue(testMonitoringGroup);
        testUserListData = new MutableLiveData<>();
        testUserListData.postValue(testUserList);

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
    public void noMonitoringGroupTest() {
        testGroupData.postValue(null);
        SystemClock.sleep(1000);

        AlertDialog dialog = currentActivity.getAlertDialog();
        assertNotNull(dialog);
        onView(withText("No monitoring group found")).check(matches(isDisplayed()));
    }

    @Test
    public void noSupervisedMemberInGroupTest() {
        testMonitoringGroup = new Group("abc123", "It's a group for testing", "0", null);
        testGroupData.postValue(testMonitoringGroup);
        SystemClock.sleep(1000);

        AlertDialog dialog = currentActivity.getAlertDialog();
        assertNotNull(dialog);
        onView(withText("No supervised member(s) found")).check(matches(isDisplayed()));
    }

    @Test
    public void searchUserTest() {
        User testUser = new User(
                "005",
                "tester",
                "#5",
                null,
                null,
                null,
                null);
        List<User> testUsersList2 = Stream.of(testUser).collect(Collectors.toList());

        currentActivity.searchUsers("tester #5");
        assertEquals(testUsersList2.get(0).getUid(),
                currentActivity.getmUsersShow().get(0).getUid());
    }

    @Test
    public void usersListViewAdapterTest() {
        ListAdapter oldListViewAdapter = currentActivity.getUsersListView().getAdapter();
        // searchUser(...) and resetUsersListViewAdapter()
        onView(allOf(withId(R.id.monitoring_group_search_users), isDisplayed()))
                .perform(replaceText("tester #5"));
        assertNotEquals(oldListViewAdapter, currentActivity.getUsersListView().getAdapter());
    }

    @Test
    public void removeOneUserFromMonitoringGroupTest() {
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
}
