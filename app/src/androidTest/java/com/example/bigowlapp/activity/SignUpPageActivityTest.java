package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.EditProfileViewModel;
import com.example.bigowlapp.viewModel.SignUpViewModel;

import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void A_unSufficientSignUpInputTest() {
        onView(withId(R.id.button_sign_up)).perform(ViewActions.scrollTo());
        onView(withId(R.id.button_sign_up))
                .check(matches(withText("Sign Up"))).perform(click());
        onView(withId(R.id.edit_text_phone)).check(matches(hasErrorText("The string supplied did not seem to be a phone number.")));
    }

    @Test
    public void B_unValidSignUpInputTest() {
        onView(withId(R.id.user_first_name)).perform(replaceText(firstName));
        onView(withId(R.id.user_last_name)).perform(replaceText(lastName));
        onView(withId(R.id.edit_text_text_mail_address)).perform(replaceText(email));
        onView(withId(R.id.edit_text_text_password)).perform(replaceText(password));
        onView(withId(R.id.edit_text_phone)).perform(replaceText("5141234567"));

        onView(withId(R.id.button_sign_up)).perform(ViewActions.scrollTo());
        onView(withId(R.id.button_sign_up))
                .check(matches(withText("Sign Up"))).perform(click());

        Espresso.onView(isRoot()).perform(waitFor(1000));
        Espresso.onView(withId(R.id.user_first_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.user_last_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_text_mail_address)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_text_password)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_phone)).check(matches(isDisplayed()));
    }

    @Test
    public void C_validSignUpInputTest() {
        //create randomness for phone number, test purpose only.
        Random rand = new Random();
        int min = 0;
        int max = Integer.MAX_VALUE;
        int result = rand.nextInt(max - min) + min;

        this.phone = String.valueOf(result);
        this.email = result + "@email.com";

        onView(withId(R.id.user_first_name)).perform(replaceText(firstName));
        onView(withId(R.id.user_last_name)).perform(replaceText(lastName));
        onView(withId(R.id.edit_text_text_mail_address)).perform(replaceText(this.email));
        onView(withId(R.id.edit_text_text_password)).perform(replaceText(password));
        onView(withId(R.id.edit_text_phone)).perform(replaceText(this.phone));

        onView(withId(R.id.button_sign_up)).perform(ViewActions.scrollTo());
        onView(withId(R.id.button_sign_up))
                .check(matches(withText("Sign Up"))).perform(click());

        Espresso.onView(isRoot()).perform(waitFor(1000));
        Espresso.onView(withId(R.id.top_app_bar)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.user_avatar)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.user_first_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.user_last_name)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_logout))
                .check(matches(withText("Log out"))).perform(click());
    }

    private ViewAction waitFor(final long ms) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SystemClock.sleep(ms);
            }
        };
    }
}