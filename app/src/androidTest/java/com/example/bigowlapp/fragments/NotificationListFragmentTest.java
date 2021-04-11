package com.example.bigowlapp.fragments;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.NullNotification;
import com.example.bigowlapp.model.ReceiveScheduleNotification;
import com.example.bigowlapp.model.ScheduleRequest;
import com.example.bigowlapp.model.SmsInvitationRequest;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.NotificationActivityViewModel;
import com.google.firebase.Timestamp;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationListFragmentTest {

    private FragmentScenario<NotificationListFragment> fragmentScenario;

    @Mock
    private NotificationActivityViewModel mockViewModel;

    // Fake data
    private List<Notification> fakeUserNotificationList;
    private LiveDataWithStatus<List<Notification>> userNotificationListData;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        // assume User data is set
        when(mockViewModel.isCurrentUserSet()).thenReturn(true);

        // setup fake data for schedules initial state
        fakeUserNotificationList = getNotificationList();
        userNotificationListData = new LiveDataWithStatus<>(fakeUserNotificationList);
        when(mockViewModel.getUserNotifications()).thenReturn(userNotificationListData);

        User fakeUser = new User("abc123", "test user", "abc", "+123", "test@mail.com", null, null);
        when(mockViewModel.getSenderUserData(anyString())).thenReturn(new LiveDataWithStatus<>(fakeUser));

        // initialize the fragment with a mocked viewModel
        fragmentScenario = FragmentScenario.launchInContainer(NotificationListFragment.class, null, R.style.AppTheme);
        fragmentScenario.moveToState(Lifecycle.State.CREATED);
        fragmentScenario.onFragment(notificationListFragment -> {
            notificationListFragment.setNotificationActivityViewModel(mockViewModel);
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void notificationsListIsViewable() {
        onView(withText(Notification.Type.SUPERVISION_REQUEST.title)).check(matches(isDisplayed()));
        onView(withText("This is a notif of type: " + Notification.Type.SUPERVISION_REQUEST)).check(matches(isDisplayed()));

        onView(withId(R.id.recyclerview_notifications))
                .check(matches(withListSize(fakeUserNotificationList.size())));
    }

    @Test
    public void clickingSupervisionRequestGoesToSupervisionResponseFragment() {
        onView(withText(Notification.Type.SUPERVISION_REQUEST.title)).perform(click());
        onView(withId(R.id.button_accept_supervision_req)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingScheduleNotifGoesToScheduleViewRespondActivity() {
        onView(withText(Notification.Type.SCHEDULE_NOTIFICATION.title)).perform(click());
        onView(withId(R.id.title_group_name)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingScheduleNotifDoesNothingIfSupervisorNotFound() {
        when(mockViewModel.getSenderUserData(anyString())).thenReturn(new LiveDataWithStatus<>(null));

        onView(withText(Notification.Type.SCHEDULE_NOTIFICATION.title)).perform(click());
        onView(withId(R.id.notification_recyclerview_title)).check(matches(isDisplayed()));
    }

    @Test
    public void noNotificationsDoesNotCauseCrash() {
        userNotificationListData.postValue(null);
        onView(withId(R.id.notification_recyclerview_title)).check(matches(isDisplayed()));
    }

    private List<Notification> getNotificationList() {
        List<Notification> notificationList = new ArrayList<>();

        notificationList.add(new Notification());
        notificationList.add(new NullNotification());
        notificationList.add(new SupervisionRequest());
        notificationList.add(new ScheduleRequest());
        notificationList.add(new AuthByPhoneNumberFailure());
        notificationList.add(new SmsInvitationRequest());
        ReceiveScheduleNotification receiveScheduleNotification = new ReceiveScheduleNotification();
        receiveScheduleNotification.setSenderUid("abc123");
        notificationList.add(receiveScheduleNotification);

        for (Notification notification : notificationList) {
            notification.setCreationTime(Timestamp.now());
            notification.setMessage("This is a notif of type: " + notification.getType());
        }

        return notificationList;
    }

    public static TypeSafeMatcher<View> withListSize(final int size) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(final View view) {
                return ((RecyclerView) view).getAdapter().getItemCount() == size;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("RecyclerView should have " + size + " items");
            }
        };
    }

}