package com.example.bigowlapp.activity;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.SendingRequestToSuperviseViewModel;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SendingRequestToSuperviseActivityTest {

    @Mock
    private SendingRequestToSuperviseViewModel sRTSViewModelMock;

    private ActivityScenario<SendingRequestToSuperviseActivity> activityScenario;

    private String currentUserId;
    private String otherUserId;
    private String supervisionRequestUid;

    @Before
    public void setUp() {
        initMocks(this);

        currentUserId = "test1";
        otherUserId = "test2";
        supervisionRequestUid = "testSR1";

        Intent testIntent = new Intent(ApplicationProvider.getApplicationContext(), SendingRequestToSuperviseActivity.class);
        User otherUser = new User();
        otherUser.setUid(otherUserId);
        testIntent.putExtra("user", otherUser);
        testIntent.putExtra("contactDetails", "testuser2");

        when(sRTSViewModelMock.isCurrentUserSet()).thenReturn(true);
        when(sRTSViewModelMock.getCurrentUserUid()).thenReturn(currentUserId);

        activityScenario = ActivityScenario.launch(testIntent);
        activityScenario.moveToState(Lifecycle.State.CREATED);
    }

    private void resumeActivity() {
        activityScenario.onActivity(activity -> activity.setsRTSViewModel(sRTSViewModelMock));
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    private SupervisionRequest createDefaultSupervisionRequest() {
        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.setUid(supervisionRequestUid);
        supervisionRequest.setReceiverUid(otherUserId);
        supervisionRequest.setSenderUid(currentUserId);
        supervisionRequest.setResponse(SupervisionRequest.Response.NEUTRAL);
        supervisionRequest.setGroupUid("");
        supervisionRequest.setCreationTime(Timestamp.now());
        return supervisionRequest;
    }

    private LiveData<List<SupervisionRequest>> createDefaultSupervisionRequestData() {
        SupervisionRequest supervisionRequest = createDefaultSupervisionRequest();
        ArrayList<SupervisionRequest> srList = new ArrayList<>();
        srList.add(supervisionRequest);
        return new MutableLiveData<>(srList);
    }

    private LiveData<List<SupervisionRequest>> createRejectedSupervisionRequestData() {
        SupervisionRequest supervisionRequest = createDefaultSupervisionRequest();
        supervisionRequest.setResponse(SupervisionRequest.Response.REJECT);
        ArrayList<SupervisionRequest> srList = new ArrayList<>();
        srList.add(supervisionRequest);
        return new MutableLiveData<>(srList);
    }

    private LiveData<List<SupervisionRequest>> createAcceptedSupervisionRequestData() {
        SupervisionRequest supervisionRequest = createDefaultSupervisionRequest();
        supervisionRequest.setResponse(SupervisionRequest.Response.ACCEPT);
        ArrayList<SupervisionRequest> srList = new ArrayList<>();
        srList.add(supervisionRequest);
        return new MutableLiveData<>(srList);
    }

    private LiveData<List<SupervisionRequest>> createEmptySupervisionRequestData() {
        return new MutableLiveData<>(null);
    }

    // This test could be in any class, but I need to add this test outside of the
    // HomePageActivity because it overrides the onClickListener of BigOwlActivity
    @Test
    public void clickOnOverflowMenu() {
        onView(withId(R.id.action_overflow)).check(matches(isDisplayed()));
        onView(withId(R.id.action_overflow)).perform(click());
    }

    @Test
    public void sendingRequestToYourself() {
        currentUserId = otherUserId;
        when(sRTSViewModelMock.getCurrentUserUid()).thenReturn(currentUserId);
        resumeActivity();

        // Button shouldn't be clickable
        onView(withId(R.id.SupRequest)).check(matches(isDisplayed()));
        onView(withId(R.id.SupRequest)).check(matches(not(isClickable())));
        onView(withId(R.id.SupRequest)).check(matches(withText("Can not send ")));
    }

    @Test
    public void sendingRequestToSomeoneNew() {
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createEmptySupervisionRequestData());
        doNothing().when(sRTSViewModelMock).sendSupervisionRequestToOtherUser(otherUserId, createDefaultSupervisionRequest());
        resumeActivity();

        // Sender Request data at this point should be null
        onView(withId(R.id.SupRequest)).check(matches(isDisplayed()));
        onView(withId(R.id.SupRequest)).check(matches(isClickable()));
        onView(withId(R.id.SupRequest)).check(matches(withText("Send request")));

        // Sending request data should contain a supervision request data at this point
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createDefaultSupervisionRequestData());
        onView(withId(R.id.SupRequest)).perform(click());
        onView(withId(R.id.SupRequest)).check(matches(withText("Cancel request")));
    }

    @Test
    public void cancelARequest() {
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createDefaultSupervisionRequestData());
        doNothing().when(sRTSViewModelMock).removeSupervisionRequestFromOtherUser(otherUserId, supervisionRequestUid);
        resumeActivity();

        // Sending request data should contain a supervision request data at this point
        onView(withId(R.id.SupRequest)).check(matches(isDisplayed()));
        onView(withId(R.id.SupRequest)).check(matches(isClickable()));
        onView(withId(R.id.SupRequest)).check(matches(withText("Cancel request")));

        // Sender Request data at this point should be null
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createEmptySupervisionRequestData());
        onView(withId(R.id.SupRequest)).perform(click());
        onView(withId(R.id.SupRequest)).check(matches(withText("Send request")));

    }


    @Test
    public void sendingRequestToSomeoneWhoAccepted() {
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createAcceptedSupervisionRequestData());
        resumeActivity();

        // Button shouldn't be clickable
        onView(withId(R.id.SupRequest)).check(matches(isDisplayed()));
        onView(withId(R.id.SupRequest)).check(matches(not(isClickable())));
        onView(withId(R.id.SupRequest)).check(matches(withText("Can not send ")));
    }


    @Test
    public void sendingRequestToSomeoneWhoRejected() {
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createRejectedSupervisionRequestData());
        doNothing().when(sRTSViewModelMock).removeSupervisionRequestFromOtherUser(otherUserId, supervisionRequestUid);
        doNothing().when(sRTSViewModelMock).sendSupervisionRequestToOtherUser(otherUserId, createDefaultSupervisionRequest());
        resumeActivity();

        // Sending request data should contain a rejected supervision request data at this point
        onView(withId(R.id.SupRequest)).check(matches(isDisplayed()));
        onView(withId(R.id.SupRequest)).check(matches(isClickable()));
        onView(withId(R.id.SupRequest)).check(matches(withText("Send new request")));

        // Sending request data should contain a neutral supervision request data at this point
        when(sRTSViewModelMock.getRequestsDataFromOtherUser(otherUserId, currentUserId))
                .thenReturn(createDefaultSupervisionRequestData());
        onView(withId(R.id.SupRequest)).perform(click());
        onView(withId(R.id.SupRequest)).check(matches(withText("Cancel request")));
    }
}