package com.example.bigowlapp.systemTests;


import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.WelcomePageActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@Ignore("Not to run on the CI")
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegisteringTest {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Test
    public void registeringTest() {

        SystemClock.sleep(7000);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.textView), withText("Don't have an account? Sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatTextView.perform(click());

        SystemClock.sleep(2000);

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text_text_person_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("system test"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_text_text_mail_address),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("systemtest@gmail.com"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_text_text_person_name), withText("system test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(click());

        SystemClock.sleep(1000);

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_text_text_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("systemtest"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.edit_text_phone),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("1231231234"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button_sign_up), withText("Sign Up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());
        SystemClock.sleep(4000);

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
