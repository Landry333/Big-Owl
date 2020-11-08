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
import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RemoveUserFromMonitoringGroupTest {

    @Rule
    public ActivityScenarioRule<MonitoringGroupPageActivity> activityRule = new ActivityScenarioRule<>(MonitoringGroupPageActivity.class);

    @Mock
    MonitoringGroupPageViewModel mockViewModel;

    MutableLiveData<Group> groupData = new MutableLiveData<>();
    MutableLiveData<List<User>> userListData = new MutableLiveData<>();

    Group g;
    List<User> users;

    @Before
    public void setUp() throws Exception {
        // Needed to make the Mockito annotations (@Mock) to work
        MockitoAnnotations.initMocks(this);

        // Initialize some fake data (could be done more clean way with methods)
        users = new ArrayList<>();

        User user1 = new User();
        user1.setFirstName("jimmy");
        user1.setLastName("jenkins");
        users.add(user1);

        User user2 = new User();
        user2.setFirstName("billybob");
        user2.setLastName("memers");
        users.add(user2);

        User user3 = new User();
        user3.setFirstName("Trump");
        user3.setLastName("Biden");
        users.add(user3);

        List<String> uids = new ArrayList<>(Arrays.asList("1", "2", "3"));
        g = new Group("uid", "its a mee, Group", "0", uids);

        // set fake data on MutableLaveData Objects
        groupData = new MutableLiveData<>();
        userListData = new MutableLiveData<>();

        // mockViewModel will give us the fake data through the MutableLaveData Objects
        when(mockViewModel.isCurrentUserSet()).thenReturn(true);
        when(mockViewModel.getGroup()).thenReturn(groupData);
        when(mockViewModel.getUsersFromGroup(any(Group.class))).thenReturn(userListData);

        // initialize activity with fake viewModel (kind of janky, Hilt di will make this not janky)
        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setmGroupPageViewModel(mockViewModel);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void basicTestToCheckUi() throws InterruptedException {
        // initial values of test
        groupData.postValue(g);
        userListData.postValue(users);

        int initialUserListSize = users.size();
        onView(withId(R.id.group_name)).check(matches(withText(g.getName())));
        onView(withId(R.id.users_list_view)).check(matches(withListSize(initialUserListSize)));
        onView(withText(users.get(0).getFullName())).check(matches(isDisplayed()));

        SystemClock.sleep(5 * 1000);

        // Data can be changed midway
        List<String> uids = new ArrayList<>(Arrays.asList("1", "2", "3"));
        Group g2 = new Group("uid", "beep boop", "meme", uids);
        groupData.postValue(g2);
        User userX = new User();
        userX.setFirstName("hello");
        userX.setLastName("there");
        users.add(userX);
        userListData.postValue(users);

        verify(mockViewModel, atLeastOnce()).getGroup();

        onView(withId(R.id.group_name)).check(matches(withText(g2.getName())));
        onView(withId(R.id.users_list_view)).check(matches(withListSize(initialUserListSize + 1)));
        onView(withText(userX.getFullName())).check(matches(isDisplayed()));

        SystemClock.sleep(5 * 1000);
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
