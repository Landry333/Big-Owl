package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.SignUpViewModel;
import com.google.android.gms.tasks.Tasks;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpPageActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpPageActivity> activityRule = new ActivityScenarioRule<>(SignUpPageActivity.class);

    @Mock
    private SignUpViewModel mockSignUpViewModel;

    private String firstName = "John";
    private String lastName = "Doe";
    private String email = "john.doe@email.com";
    private String password = "abc123";
    private String phone;

    SignUpPageActivity signUpPageActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setSignUpViewModel(mockSignUpViewModel);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void unSufficientSignUpInputTest() {
        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());
        onView(withId(R.id.edit_text_phone)).check(matches(hasErrorText("The string supplied did not seem to be a phone number.")));
    }

    @Test
    public void emptyFirstName(){
        //this has to be added so the error for firstName can be display.
        onView(withId(R.id.edit_text_phone)).perform(typeText("5141234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        onView(withId(R.id.user_first_name)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_first_name)).check(matches(hasErrorText("Please enter your first name")));
    }

    @Test
    public void emptyLastName(){
        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText("5141234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        onView(withId(R.id.user_last_name)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).check(matches(hasErrorText("Please enter your last name")));
    }

    @Test
    public void emptyEmail(){
        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).perform(typeText(lastName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText("5141234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        onView(withId(R.id.edit_text_text_mail_address)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_mail_address)).check(matches(hasErrorText("Please enter a valid email")));
    }

    @Test
    public void emptyPassword(){
        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).perform(typeText(lastName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_mail_address)).perform(typeText(email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText("5141234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        onView(withId(R.id.edit_text_text_password)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_password)).check(matches(hasErrorText("Please enter your password")));
    }

    @Test
    public void unValidSignUpInputTest() {
        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).perform(typeText(lastName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_mail_address)).perform(typeText(email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_password)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText("5141234567"), ViewActions.closeSoftKeyboard());

        Mockito.when(mockSignUpViewModel.createUser(any(), any(), any(), any(), any())).thenReturn(Tasks.forException(new Exception("Failed to sign up")));

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        Espresso.onView(withId(R.id.user_first_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.user_last_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_text_mail_address)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_text_password)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_phone)).check(matches(isDisplayed()));
    }

    @Test
    public void validSignUpInputTest() {
        //create randomness for phone number, test purpose only.
        Random rand = new Random();
        int min = 0;
        int max = Integer.MAX_VALUE;
        int result = rand.nextInt(max - min) + min;

        this.phone = String.valueOf(result);
        this.phone = "+1" + this.phone;
        this.email = result + "@email.com";

        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).perform(typeText(lastName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_mail_address)).perform(typeText(this.email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_password)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText(this.phone), ViewActions.closeSoftKeyboard());

        Mockito.when(mockSignUpViewModel.createUser(email, password, this.phone, firstName, lastName)).thenReturn(Tasks.forResult(null));

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        Mockito.verify(mockSignUpViewModel).createUser(this.email, password, this.phone, firstName, lastName);
    }

    @Test
    public void tvSignInButtonTest(){
        onView(withId(R.id.text_view_sign_in))
                .perform(scrollTo())
                .check(matches(withText("Already have an account? Sign in here"))).perform(click());

        onView(withId(R.id.editTextTextEmailAddress)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPassword)).check(matches(isDisplayed()));
    }
}