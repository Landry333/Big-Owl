package com.example.bigowlapp.systemTests;


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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
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
public class SetScheduleTest {

    @Rule
    public ActivityTestRule<WelcomePageActivity> mActivityTestRule = new ActivityTestRule<>(WelcomePageActivity.class);

    @Test
    public void setScheduleTest() throws InterruptedException {
        Thread.sleep(5000);

        //Set Schedule click
        ViewInteraction appCompatButton3 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.btn_set_schedule), ViewMatchers.withText("Set Schedule"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.table_layout),
                                        3),
                                0)));
        appCompatButton3.perform(ViewActions.scrollTo(), ViewActions.click());

        Thread.sleep(1000);

        //Mod title
        onView(withId(R.id.edit_title_schedule)).perform(replaceText("System_test"));

        Thread.sleep(1000);
        //Modify date
        onView(withId(R.id.edit_start_date)).perform(click());
        Thread.sleep(1000);
        onView(withClassName((Matchers.equalTo(DatePicker.class.getName())))).perform(PickerActions.setDate(2021, 3, 12));
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

        ViewInteraction recyclerView4 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.user_item_list_recycler_view),
                        childAtPosition(
                                ViewMatchers.withId(R.id.user_item_list_coordinator_layout),
                                0)));
        recyclerView4.perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

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
        Thread.sleep(1000);

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

//    private void setupViews() {
//        // ScheduleFormFragment Views
//        editTitle = onView(withId(R.id.edit_title_schedule));
//        groupButton = onView(withId(R.id.select_group_button));
//        selectUserButton = onView(withId(R.id.select_user_button));
//        usersListView = onView(withId(R.id.select_users_list_view));
//        editStartDate = onView(withId(R.id.edit_start_date));
//        editStartTime = onView(withId(R.id.edit_start_time));
//        editEndDate = onView(withId(R.id.edit_end_date));
//        editEndTime = onView(withId(R.id.edit_end_time));
//        confirmSetSchedule = onView(withId(R.id.set_schedule_confirm_button));
//        editLocation = onView(withId(R.id.edit_location));
//        // User Fragment Views
//        confirmUserselection = onView(withId(R.id.user_item_list_confirm));
//        cancelUserselection = onView(withId(R.id.user_item_list_cancel));
//    }

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
