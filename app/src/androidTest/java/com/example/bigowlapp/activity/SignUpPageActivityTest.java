package com.example.bigowlapp.activity;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.SignUpViewModel;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpPageActivityTest {

    @Rule
    public ActivityTestRule<SignUpPageActivity> activityRule = new ActivityTestRule<>(SignUpPageActivity.class);

    @Mock
    private SignUpViewModel mockSignUpViewModel;

    private String firstName = "John";
    private String lastName = "Doe";
    private String email = "john.doe@email.com";
    private String password = "abc123";
    private String phone;

    SignUpPageActivity signUpPageActivity;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void unSufficientSignUpInputTest() {
        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());
        onView(withId(R.id.edit_text_phone)).check(matches(hasErrorText("The string supplied did not seem to be a phone number.")));
    }

    @Test
    public void unValidSignUpInputTest() {
        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).perform(typeText(lastName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_mail_address)).perform(typeText(email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_password)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText("5141234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());

        Espresso.onView(isRoot()).perform(waitFor(1000));
        Espresso.onView(withId(R.id.user_first_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.user_last_name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_text_mail_address)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_text_password)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.edit_text_phone)).check(matches(isDisplayed()));
    }

    @Test
    public void validSignUpInputTest() {
        //create randomness for phone number, test purpose only.
        Random rand = new Random();
        int min = 0;
        int max = Integer.MAX_VALUE;
        int result = rand.nextInt(max - min) + min;

        this.phone = String.valueOf(result);
        this.email = result + "@email.com";

        onView(withId(R.id.user_first_name)).perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.user_last_name)).perform(typeText(lastName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_mail_address)).perform(typeText(this.email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_text_password)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edit_text_phone)).perform(typeText(this.phone), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button_sign_up))
                .perform(scrollTo())
                .check(matches(withText("Sign Up"))).perform(click());
    }

    private ViewAction waitFor(final long ms) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SystemClock.sleep(ms);
            }
        };
    }

    public static ViewAction nestedScrollTo() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return Matchers.allOf(
                        isDescendantOfA(isAssignableFrom(NestedScrollView.class)),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
            }

            @Override
            public String getDescription() {
                return "View is not NestedScrollView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                try {
                    NestedScrollView nestedScrollView = (NestedScrollView)
                            findFirstParentLayoutOfClass(view, NestedScrollView.class);
                    if (nestedScrollView != null) {
                        nestedScrollView.scrollTo(0, view.getTop());
                    } else {
                        throw new Exception("Unable to find NestedScrollView parent.");
                    }
                } catch (Exception e) {
                    throw new PerformException.Builder()
                            .withActionDescription(this.getDescription())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(e)
                            .build();
                }
                uiController.loopMainThreadUntilIdle();
            }

        };
    }

    private static View findFirstParentLayoutOfClass(View view, Class<? extends View> parentClass) {
        ViewParent parent = new FrameLayout(view.getContext());
        ViewParent incrementView = null;
        int i = 0;
        while (parent != null && !(parent.getClass() == parentClass)) {
            if (i == 0) {
                parent = findParent(view);
            } else {
                parent = findParent(incrementView);
            }
            incrementView = parent;
            i++;
        }
        return (View) parent;
    }

    private static ViewParent findParent(View view) {
        return view.getParent();
    }

    private static ViewParent findParent(ViewParent view) {
        return view.getParent();
    }


}