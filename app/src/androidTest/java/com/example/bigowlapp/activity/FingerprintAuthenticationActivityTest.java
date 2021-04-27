package com.example.bigowlapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.example.bigowlapp.view_model.HomePageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FingerprintAuthenticationActivityTest {

    @Rule
    public ActivityScenarioRule<FingerprintAuthenticationActivity> activityRule = new ActivityScenarioRule<>(FingerprintAuthenticationActivity.class);

    @Mock
    private HomePageViewModel homePageViewModel;

    @Mock
    private PhoneNumberFormatter phoneNumberFormatter;


    private User testUser;
    private LiveDataWithStatus<User> testUserData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new User();
        testUser = new User("abc123", "test user", "abc", "+123", "test@mail.com", "yes");
        testUserData = new LiveDataWithStatus<>();

        when(homePageViewModel.isCurrentUserSet()).thenReturn(true);
        when(homePageViewModel.getCurrentUserData()).thenReturn(testUserData);
        when(homePageViewModel.getCurrentUserUid()).thenReturn(testUser.getUid());

        when(phoneNumberFormatter.getFormattedSMSNumber()).thenReturn(testUser.getPhoneNumber());

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setHomePageViewModel(homePageViewModel);
            activity.setPhoneNumberFormatter(phoneNumberFormatter);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void clickOnLogout() {
        onView(withId(R.id.btn_logout)).check(matches(withText("Log out")));
        onView(withId(R.id.btn_logout)).perform(click());
    }

    @Test
    public void noCompatibilityForFingerprintAuthenticationTest() {
        when(phoneNumberFormatter.getFormattedSMSNumber()).thenReturn("456");
        testUser.setFingerprintAuthRegistration("No");
        testUserData.postValue(testUser);
        onView(withId(R.id.btn_go_to_home_page)).check(matches(isDisplayed()));
        //onView(withId(R.id.fingerprint_auth_registration_text)).check(matches(withText("Sorry, this additional security service is not available on\n        this number or with your phone and telephony provider\n\n\n        SERVICE IS NOT ALLOWED\n\n\n        First make sure sim card_1 number on this phone is the same as in your account")));
        onView(withId(R.id.fingerprint_auth_registration_text)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_go_to_home_page)).perform(click());
    }

    @Test
    public void notAllowedFingerprintAuthenticationTest() {
        when(phoneNumberFormatter.getFormattedSMSNumber()).thenReturn("456");
        testUser.setFingerprintAuthRegistration("yes");
        testUserData.postValue(testUser);
        onView(withId(R.id.fingerprint_auth_registration_text)).check(matches(isDisplayed()));
    }

    @Test
    public void addFingerprintAuthenticationTest() {
        when(phoneNumberFormatter.getFormattedSMSNumber()).thenReturn("+123");
        testUser.setFingerprintAuthRegistration("no");
        testUserData.postValue(testUser);
        onView(withId(R.id.fingerprint_auth_add_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.fingerprint_auth_maybe_later_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.fingerprint_auth_registration_text)).check(matches(isDisplayed()));
        onView(withId(R.id.fingerprint_auth_add_btn)).perform(click());
    }

    @Test
    public void maybeLaterForFingerprintAuthenticationTest() {
        when(phoneNumberFormatter.getFormattedSMSNumber()).thenReturn("+123");
        testUser.setFingerprintAuthRegistration("no");
        testUserData.postValue(testUser);
        onView(withId(R.id.fingerprint_auth_add_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.fingerprint_auth_maybe_later_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.fingerprint_auth_registration_text)).check(matches(isDisplayed()));
        onView(withId(R.id.fingerprint_auth_maybe_later_btn)).perform(click());
    }

    @Test
    public void startFingerprintAuthenticationTest() {
        when(phoneNumberFormatter.getFormattedSMSNumber()).thenReturn("+123");
        testUser.setFingerprintAuthRegistration("yes");
        testUserData.postValue(testUser);
        onView(withId(R.id.fingerprint_auth_registration_text)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_start_authentication)).check(matches(withText("start fingerprint authentication")));
        onView(withId(R.id.btn_start_authentication)).perform(click());
    }
}
