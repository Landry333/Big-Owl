package com.example.bigowlapp.activity;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.SearchContactsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchContactsToSuperviseTest {

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_CONTACTS);

    @Mock
    private SearchContactsViewModel mockViewModel;

    LiveDataWithStatus<User> userToAddData;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        userToAddData = new LiveDataWithStatus<>(null);
        when(mockViewModel.getUserToAdd(any())).thenReturn(userToAddData);

        when(mockViewModel.populateContactsList(any(), any())).thenReturn(new ArrayList<>());

        ActivityScenario<SearchContactsToSupervise> scenario = ActivityScenario.launch(SearchContactsToSupervise.class);
        scenario.onActivity(activity ->
                activity.setSearchContactsViewModel(mockViewModel));
    }

    @Test
    public void searchForContactsSearchBarWorking() {
        onView(withId(R.id.search_users)).perform(replaceText("Search String"));
        onView(withId(R.id.search_users)).check(matches(withText("Search String")));
        onView(withId(R.id.search_users)).perform(replaceText(""));
        onView(withId(R.id.search_users)).check(matches(withText("")));
    }

    @Test
    public void searchForNonRegisteredUserGoesToSmsInvitationPage() {
        userToAddData.postValue(null);
        List<String> fakeContactData = getFakeContactsData(8);
        when(mockViewModel.populateContactsList(any(), any())).thenReturn(fakeContactData);

        onView(withId(R.id.search_users)).perform(replaceText("Joe"));
        onView(withText(fakeContactData.get(0))).perform(click());
        onView(withId(R.id.cancel_sms_invitation)).check(matches(isDisplayed()));
    }

    @Test
    public void searchForRegisteredUserGoesToSendRequestPage() {
        User registeredUser = new User();
        registeredUser.setFirstName("Joe");
        registeredUser.setLastName("Doe3");

        userToAddData.postValue(registeredUser);
        List<String> fakeContactData = getFakeContactsData(5);
        when(mockViewModel.populateContactsList(any(), any())).thenReturn(fakeContactData);

        onView(withId(R.id.search_users)).perform(replaceText("Joe"));
        onView(withText(fakeContactData.get(2))).perform(click());
        onView(withId(R.id.sup_request)).check(matches(isDisplayed()));
    }

    private List<String> getFakeContactsData(int listSize) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            data.add("Joe Doe" + i + "\n+1438123456" + i);
        }
        return data;
    }
}