package com.example.bigowlapp.systemTests;


import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@Ignore("Not to run on the CI")
@LargeTest
@RunWith(AndroidJUnit4.class)
public class InviteByPhoneTest {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Test
    public void inviteByPhoneTest() {

        SystemClock.sleep(7000);
        try {
            ViewInteraction appCompatEditText = onView(
                    allOf(withId(R.id.editTextTextEmailAddress),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    0),
                            isDisplayed()));
            appCompatEditText.perform(replaceText("espressotest@gmail.com"), closeSoftKeyboard());

            SystemClock.sleep(1000);

            ViewInteraction appCompatEditText2 = onView(
                    allOf(withId(R.id.editTextTextPassword),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    2),
                            isDisplayed()));
            appCompatEditText2.perform(replaceText("espressotest"), closeSoftKeyboard());

            SystemClock.sleep(1000);

            ViewInteraction appCompatButton = onView(
                    allOf(withId(R.id.button), withText("Sign In"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    1),
                            isDisplayed()));
            appCompatButton.perform(click());

            SystemClock.sleep(5000);

        } catch (Exception e) {
        }
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_add_users), withText("Add Users"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableRow")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        SystemClock.sleep(2000);

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnPhone), withText("Invite By Phone Number"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton3.perform(click());

        SystemClock.sleep(2000);

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.search_users),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("+1234567890"), closeSoftKeyboard());

        SystemClock.sleep(2000);

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.get_users), withText("Search User"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton4.perform(click());

        SystemClock.sleep(2000);

        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.listContacts),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                3)))
                .atPosition(0);
        textView.perform(click());

        SystemClock.sleep(3000);

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.search_users), withText("+1234567890"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("+12345867876"));

        SystemClock.sleep(2000);

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.search_users), withText("+12345867876"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        SystemClock.sleep(2000);

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.get_users), withText("Search User"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton5.perform(click());

        SystemClock.sleep(3000);

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
