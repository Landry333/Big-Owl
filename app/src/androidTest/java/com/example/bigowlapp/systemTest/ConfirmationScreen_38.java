package com.example.bigowlapp.systemTest;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.WelcomePageActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ConfirmationScreen_38 {

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
    public void confirmationScreen_38() throws InterruptedException {

//        Thread.sleep(5000);
//
//        ViewInteraction appCompatEditText = onView(
//                allOf(withId(R.id.editTextTextEmailAddress),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                2)));
//        appCompatEditText.perform(scrollTo(), replaceText("joe1@email.com"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText2 = onView(
//                allOf(withId(R.id.editTextTextPassword),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                3)));
//        appCompatEditText2.perform(scrollTo(), replaceText("joedoe1"), closeSoftKeyboard());
//
//        ViewInteraction appCompatButton2 = onView(
//                allOf(withId(R.id.button), withText("Sign In"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.ScrollView")),
//                                        0),
//                                4)));
//        appCompatButton2.perform(scrollTo(), click());

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
        Thread.sleep(3000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_set_schedule), withText("create schedule"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());
        Thread.sleep(1200);

        //Mod title
        onView(withId(R.id.edit_title_schedule)).perform(replaceText("System_test_R3"));

        Thread.sleep(1000);
        //Modify date
        onView(withId(R.id.edit_start_date)).perform(click());
        Thread.sleep(1000);
        onView(withClassName((Matchers.equalTo(DatePicker.class.getName())))).perform(PickerActions.setDate(2021, 4, 12));
        Thread.sleep(1000);
        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        //Modify Start time
        onView(withId(R.id.edit_start_time)).perform(click());
        Thread.sleep(1000);
        onView(withClassName((Matchers.equalTo(TimePicker.class.getName())))).perform(PickerActions.setTime(9, 10));
        Thread.sleep(1000);
        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        //Modify End date
        onView(withId(R.id.edit_end_date)).perform(click());
        Thread.sleep(1000);
        onView(withClassName((Matchers.equalTo(DatePicker.class.getName())))).perform(PickerActions.setDate(2023, 6, 15));
        Thread.sleep(1000);
        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        //Modify End time
        onView(withId(R.id.edit_end_time)).perform(click());
        Thread.sleep(1000);
        onView(withClassName((Matchers.equalTo(TimePicker.class.getName())))).perform(PickerActions.setTime(16, 23));
        Thread.sleep(1000);
        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton10 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.edit_location), ViewMatchers.withText("Select Location"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.location),
                                        0),
                                1)));
        appCompatButton10.perform(ViewActions.scrollTo(), ViewActions.click());

        Thread.sleep(1000);

        ViewInteraction appCompatEditText9 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.edittext_search),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.searchView),
                                        0),
                                0),
                        ViewMatchers.isDisplayed()));
        appCompatEditText9.perform(ViewActions.replaceText("montreal"), ViewActions.closeSoftKeyboard());

        Thread.sleep(1000);

        onView(withIndex(withText("Montréal"), 0)).perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton11 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.select_group_button), ViewMatchers.withText("Select Group"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.group_selection),
                                        0),
                                1)));
        appCompatButton11.perform(ViewActions.scrollTo(), ViewActions.click());

        Thread.sleep(1000);

        ViewInteraction recyclerView2 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.list),
                        childAtPosition(
                                ViewMatchers.withId(android.R.id.content),
                                0)));
        recyclerView2.perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        Thread.sleep(1000);

        ViewInteraction appCompatButton12 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.select_user_button), ViewMatchers.withText("Select User(s)"),
                        childAtPosition(
                                Matchers.allOf(ViewMatchers.withId(R.id.select_users_container),
                                        childAtPosition(
                                                ViewMatchers.withId(R.id.user_selection),
                                                0)),
                                1)));
        appCompatButton12.perform(ViewActions.scrollTo(), ViewActions.click());

        Thread.sleep(1000);

        ViewInteraction recyclerView3 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.user_item_list_recycler_view),
                        childAtPosition(
                                ViewMatchers.withId(R.id.user_item_list_coordinator_layout),
                                0)));
        recyclerView3.perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.click()));

        Thread.sleep(1000);

        ViewInteraction appCompatButton13 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.user_item_list_confirm), ViewMatchers.withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.user_item_list_coordinator_layout),
                                        1),
                                1),
                        ViewMatchers.isDisplayed()));
        appCompatButton13.perform(ViewActions.click());

        Thread.sleep(1000);

        ViewInteraction appCompatButton14 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.set_schedule_confirm_button), ViewMatchers.withText("Set Schedule"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatButton14.perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(5000);

        ViewInteraction appCompatButton15 = onView(
                allOf(withId(android.R.id.button1), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton15.perform(scrollTo(), click());
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

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}
