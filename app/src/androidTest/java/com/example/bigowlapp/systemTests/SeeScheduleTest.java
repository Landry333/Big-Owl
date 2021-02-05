package com.example.bigowlapp.systemTests;


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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SeeScheduleTest {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Test
    public void seeScheduleTest() throws InterruptedException {
        Thread.sleep(5000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_supervised_group), withText("Supervised Group"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableRow")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        Thread.sleep(1000);

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.supervised_groups),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        constraintLayout.perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_schedule_list), withText("Schedules"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                2)));
        appCompatButton3.perform(scrollTo(), click());

        Thread.sleep(1000);

        DataInteraction constraintLayout2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.schedule_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(4);
        constraintLayout2.perform(click());

        Thread.sleep(2000);

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.button_accept), withText("Accept"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        5),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        Thread.sleep(2000);

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.button_reject), withText("Reject"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        5),
                                1),
                        isDisplayed()));
        appCompatButton5.perform(click());
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
