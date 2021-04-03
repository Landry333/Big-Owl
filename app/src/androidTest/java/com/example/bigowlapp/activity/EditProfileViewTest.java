package com.example.bigowlapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.EditProfileViewModel;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileViewTest {

    @Rule
    public ActivityScenarioRule<EditProfileActivity> activityRule = new ActivityScenarioRule<>(EditProfileActivity.class);

    @Mock
    private EditProfileViewModel mockEditProfileViewModel;

    private User testUser;
    EditProfileActivity editProfileActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new User("abc123",
                "BeforeEditFirstName",
                "BeforeEditLastName",
                "+1234567890",
                "tester@mail.com",
                null,
                null,
                "yes");
        MutableLiveData<User> testUserData = new MutableLiveData<>();

        when(mockEditProfileViewModel.isCurrentUserSet()).thenReturn(true);
        when(mockEditProfileViewModel.getCurrentUserData()).thenReturn(testUserData);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            editProfileActivity = activity;
            activity.setHomePageViewModel(mockEditProfileViewModel);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);

        testUserData.postValue(testUser);
    }

    @Test
    public void editProfileAtEditProfileActivityTest() {
        when(mockEditProfileViewModel.isPhoneNumberTaken("+1111111111")).thenReturn(Tasks.forResult(null));
        onView(withId(R.id.edit_user_first_name))
                .check(matches(withText(testUser.getFirstName())));
        onView(withId(R.id.edit_user_last_name))
                .check(matches(withText(testUser.getLastName())));
        onView(withId(R.id.edit_user_phone_number))
                .check(matches(withText(testUser.getPhoneNumber())));
        onView(withId(R.id.edit_fingerprint_auth_registration))
                .check(matches(withText(testUser.getFingerprintAuthRegistration())));
        onView(withId(R.id.edit_user_image_url))
                .check(matches(withText("")));

        onView(withId(R.id.edit_user_first_name)).perform(replaceText("AfterEditFirstName"));
        onView(withId(R.id.edit_user_last_name)).perform(replaceText("AfterEditLastName"));
        onView(withId(R.id.edit_user_phone_number)).perform(replaceText("+1111111111"));
        onView(withId(R.id.edit_fingerprint_auth_registration)).perform(replaceText("yes"));
        onView(withId(R.id.edit_user_image_url)).perform(replaceText("https://simpleicon.com/wp-content/uploads/user1.png"));

        onView(withId(R.id.edit_button_confirm))
                .check(matches(withText("Confirm"))).perform(click());

        verify(mockEditProfileViewModel, atMostOnce()).editUserProfile(
                "AfterEditFirstName",
                "AfterEditLastName",
                "yes",
                "+1111111111",
                "https://simpleicon.com/wp-content/uploads/user1.png"
        );
    }

    @Test
    public void editProfileWithoutInputtingAnyPrimaryFieldsTest() {
        onView(withId(R.id.edit_user_first_name)).perform(click()).perform(replaceText(""));
        onView(withId(R.id.edit_button_confirm))
                .check(matches(withText("Confirm"))).perform(click());
        onView(withId(R.id.edit_user_first_name)).check(matches(hasErrorText("Please enter a valid first name.")));

        onView(withId(R.id.edit_user_last_name)).perform(click()).perform(replaceText(""));
        onView(withId(R.id.edit_button_confirm))
                .check(matches(withText("Confirm"))).perform(click());
        onView(withId(R.id.edit_user_last_name)).check(matches(hasErrorText("Please enter a valid last name.")));

        onView(withId(R.id.edit_user_phone_number)).perform(click()).perform(replaceText(""));
        onView(withId(R.id.edit_button_confirm))
                .check(matches(withText("Confirm"))).perform(click());
        onView(withId(R.id.edit_user_phone_number)).check(matches(hasErrorText("Please enter a valid phone number.")));

        onView(withId(R.id.edit_fingerprint_auth_registration)).perform(click()).perform(replaceText(""));
        onView(withId(R.id.edit_button_confirm))
                .check(matches(withText("Confirm"))).perform(click());
        onView(withId(R.id.edit_fingerprint_auth_registration)).check(matches(hasErrorText("Please enter YES or NO and remove any empty space.")));
    }

    @Test
    public void clickingTheCancelButtonOnScreenTest() {
        onView(withId(R.id.edit_button_cancel))
                .check(matches(withText("Cancel"))).perform(click());

        verify(mockEditProfileViewModel, never()).editUserProfile(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

}
