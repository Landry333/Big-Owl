package com.example.bigowlapp.systemTests;


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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditProfile_Homebutton {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Test
    public void editProfile_Homebutton() throws InterruptedException {
        Thread.sleep(5000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.action_overflow), withContentDescription("overflow menu"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.top_app_bar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        Thread.sleep(3000);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Edit Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        Thread.sleep(2000);

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_user_first_name), //withText("testUser1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("John Smith"));

        Thread.sleep(1000);

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_user_first_name), //withText("testUser_systemTest"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        Thread.sleep(1000);

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.edit_user_last_name), //withText("UsabilityTesting"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("SystemTest"));

        Thread.sleep(1000);

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.edit_user_phone_number), //withText("123987"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("1234567123"));

        Thread.sleep(1000);

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.edit_button_confirm), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_add_users), withText("Add Users"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.table_layout),
                                        0),
                                0)));
        appCompatButton2.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.action_overflow), withContentDescription("overflow menu"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.top_app_bar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        Thread.sleep(2000);

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Home"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        Thread.sleep(1500);

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
