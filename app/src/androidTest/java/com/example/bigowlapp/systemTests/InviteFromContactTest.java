package com.example.bigowlapp.systemTests;


import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.WelcomePageActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
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

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InviteFromContactTest {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_CONTACTS");

    @Test
    public void inviteFromContactTest() {

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
                allOf(withId(R.id.btnAddUsers), withText("Add Users"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableRow")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        SystemClock.sleep(2000);

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnContacts), withText("Invite From Contact List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());

        SystemClock.sleep(2000);

        DataInteraction textView = onData(anything())
                .inAdapterView(allOf(withId(R.id.listContacts),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                2)))
                .atPosition(0);
        textView.perform(click());

        SystemClock.sleep(2000);

        DataInteraction textView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.listContacts),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                2)))
                .atPosition(1);
        textView2.perform(click());

        SystemClock.sleep(2000);

        DataInteraction textView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.listContacts),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                2)))
                .atPosition(2);
        textView3.perform(click());

        SystemClock.sleep(2000);

        pressBack();

        SystemClock.sleep(2000);

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.search_users),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("ref"), closeSoftKeyboard());

        SystemClock.sleep(3000);

        DataInteraction textView4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.listContacts),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                2)))
                .atPosition(0);
        textView4.perform(click());

        SystemClock.sleep(2000);

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
