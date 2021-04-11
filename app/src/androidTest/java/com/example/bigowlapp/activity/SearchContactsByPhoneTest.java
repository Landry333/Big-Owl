package com.example.bigowlapp.activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.SearchContactsViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchContactsByPhoneTest {

    @Mock
    private SearchContactsViewModel mockViewModel;

    private LiveDataWithStatus<User> userToAddData;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        userToAddData = new LiveDataWithStatus<>(null);
        when(mockViewModel.getUserToAdd(any())).thenReturn(userToAddData);

        ActivityScenario<SearchContactsByPhone> scenario = ActivityScenario.launch(SearchContactsByPhone.class);
        scenario.onActivity(activity ->
                activity.setSearchContactsViewModel(mockViewModel));
    }

    @Test
    public void searchForNonRegisteredUserGoesToSmsInvitationPage() {
        userToAddData.postValue(null);

        onView(withId(R.id.search_users)).perform(replaceText("+14388765544"));
        onView(withId(R.id.get_users)).perform(click());
        onView(withId(R.id.cancel_sms_invitation)).check(matches(isDisplayed()));
    }

    @Test
    public void searchForRegisteredUserGoesToSendRequestPage() {
        User registeredUser = new User();
        registeredUser.setFirstName("Joe");
        registeredUser.setLastName("Doe3");

        userToAddData.postValue(registeredUser);

        onView(withId(R.id.search_users)).perform(replaceText("+14388760079"));
        onView(withId(R.id.get_users)).perform(click());
        onView(withId(R.id.sup_request)).check(matches(isDisplayed()));
    }

    @Test
    public void searchWithImpossibleNumberShowsError() {
        userToAddData.postValue(null);

        String notAPhoneNumber = "";
        onView(withId(R.id.search_users)).perform(replaceText(notAPhoneNumber));
        onView(withId(R.id.get_users)).perform(click());
        onView(withId(R.id.search_users))
                .check(matches(hasErrorText("Please enter a valid phone number.")));
    }
}