package com.example.bigowlapp.activity;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.bigowlapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;



@LargeTest
@RunWith(AndroidJUnit4.class)
public class FingerprintAuthenticationActivityTest2 {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.SEND_SMS",
                    "android.permission.RECEIVE_SMS",
                    "android.permission.READ_SMS",
                    "android.permission.READ_PHONE_NUMBERS",
                    "android.permission.READ_PHONE_STATE");

    @Test
    public void fingerprintAuthenticationActivityTest3() throws InterruptedException{
        Thread.sleep(5000);
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextTextEmailAddress),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("testUser25@email.com"), closeSoftKeyboard());

        Thread.sleep(3000);
        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.editTextTextEmailAddress), withText("testUser25@email.com"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText7.perform(closeSoftKeyboard());

        Thread.sleep(3000);
        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.editTextTextPassword),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatEditText8.perform(scrollTo(), replaceText("testuser25"), closeSoftKeyboard());

        Thread.sleep(3000);
        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.editTextTextPassword), withText("testuser25"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatEditText9.perform(pressImeActionButton());

        Thread.sleep(3000);
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button), withText("Sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatButton2.perform(scrollTo(), click());

        Thread.sleep(3000);
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_start_authentication), withText("start fingerprint authentication"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton3.perform(click());
        Thread.sleep(3000);
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

