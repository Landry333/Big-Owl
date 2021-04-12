package com.example.bigowlapp.systemTest;


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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ViewScheduleReport_25 {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

//    @Rule
//    public GrantPermissionRule mGrantPermissionRule =
//            GrantPermissionRule.grant(
//                    "android.permission.SEND_SMS",
//                    "android.permission.RECEIVE_SMS",
//                    "android.permission.READ_SMS",
//                    "android.permission.READ_PHONE_NUMBERS",
//                    "android.permission.READ_PHONE_STATE");

    @Test
    public void viewScheduleReport_25()  throws InterruptedException {
//        ViewInteraction appCompatButton = onView(
//                allOf(withId(android.R.id.button1), withText("Ok"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.buttonPanel),
//                                        0),
//                                3)));
//        appCompatButton.perform(scrollTo(), click());
////
//        Thread.sleep(5000);
//
//        ViewInteraction appCompatEditText = onView(
//                allOf(withId(R.id.editTextTextEmailAddress),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                2)));
//        appCompatEditText.perform(scrollTo(), click());
//
//        ViewInteraction appCompatEditText2 = onView(
//                allOf(withId(R.id.editTextTextEmailAddress),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                2)));
//        appCompatEditText2.perform(scrollTo(), replaceText("joe1@email.com"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText3 = onView(
//                allOf(withId(R.id.editTextTextPassword),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                3)));
//        appCompatEditText3.perform(scrollTo(), replaceText("joedoe1"), closeSoftKeyboard());
//
//        ViewInteraction appCompatButton1 = onView(
//                allOf(withId(R.id.button), withText("Sign In"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                4)));
//        appCompatButton1.perform(scrollTo(), click());

        Thread.sleep(5000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.action_schedule), withContentDescription("schedule"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.top_app_bar),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_view_schedule), withText("View Schedule"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        Thread.sleep(2000);

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.schedule_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(2);
        constraintLayout.perform(click());
        Thread.sleep(2000);
        pressBack();
        Thread.sleep(2000);
        DataInteraction constraintLayout2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.schedule_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(1);
        constraintLayout2.perform(click());
        Thread.sleep(2000);
        pressBack();
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
