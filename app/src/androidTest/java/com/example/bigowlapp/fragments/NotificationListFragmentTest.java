package com.example.bigowlapp.fragments;

import android.os.SystemClock;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
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
import com.example.bigowlapp.view_model.NotificationActivityViewModel;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationListFragmentTest {

    private FragmentScenario<NotificationListFragment> fragmentScenario;
    private NotificationListFragment notificationListFragment;

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

        // initialize the fragment with a mocked viewModel
        fragmentScenario = FragmentScenario.launchInContainer(NotificationListFragment.class, null, R.style.AppTheme);
        fragmentScenario.moveToState(Lifecycle.State.CREATED);
        fragmentScenario.onFragment(notificationListFragment -> {
            notificationListFragment.setNotificationActivityViewModel(mockViewModel);
            this.notificationListFragment = notificationListFragment;
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void notificationsListIsViewable() {
        SystemClock.sleep(3 * 1000);
        fakeUserNotificationList.get(0).setMessage("This is a realy super duper ultra megar expiaalodouscis messgae for the user to read");
        userNotificationListData.postValue(fakeUserNotificationList);
        SystemClock.sleep(10 * 1000);
    }

    private List<Notification> getNotificationList() {
        List<Notification> notificationList = new ArrayList<>();

        notificationList.add(new Notification());
        notificationList.add(new NullNotification());
        notificationList.add(new SupervisionRequest());
        notificationList.add(new ScheduleRequest());
        notificationList.add(new AuthByPhoneNumberFailure());
        notificationList.add(new SmsInvitationRequest());
        notificationList.add(new ReceiveScheduleNotification());

        notificationList.add(new Notification());
        notificationList.add(new NullNotification());
        notificationList.add(new SupervisionRequest());
        notificationList.add(new ScheduleRequest());
        notificationList.add(new AuthByPhoneNumberFailure());
        notificationList.add(new SmsInvitationRequest());
        notificationList.add(new ReceiveScheduleNotification());

        for (Notification notification : notificationList) {
            notification.setCreationTime(Timestamp.now());
            notification.setMessage("This is a notif of type: " + notification.getType());
        }

        return notificationList;
    }

}