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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SendSuperviseReqTest {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Test
    public void sendSuperviseReqTest() throws InterruptedException {

        Thread.sleep(5000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_add_users), withText("Add Users"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.table_layout),
                                        0),
                                0)));
        appCompatButton2.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnPhone), withText("Invite By Phone Number"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.search_users),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("5142222222"), closeSoftKeyboard());

        Thread.sleep(1000);

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.get_users), withText("Search User"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton4.perform(click());

        Thread.sleep(2000);

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.SupRequest), withText("Send a request to supervise this user"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton5.perform(click());

        Thread.sleep(2000);

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.SupRequest), withText("You have sent a request already. Cancel request"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton6.perform(click());

        Thread.sleep(2000);

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
