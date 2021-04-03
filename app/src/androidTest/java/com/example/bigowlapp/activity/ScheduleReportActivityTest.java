package com.example.bigowlapp.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.viewModel.ScheduleReportViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScheduleReportActivityTest {
    private static ScheduleReportActivity currentActivity;
    private static Schedule testSchedule;
    private final Timestamp timestampNow = Timestamp.now();
    private MutableLiveData<Schedule> testScheduleData;
    private ListViewMatcher listViewMatcher;

    @Mock
    private ScheduleReportViewModel mockScheduleReportViewModel;

    @Before
    public void setUp() {
        initMocks(this);

        User testSupervisor = new User(
                "supervisor001",
                "supervisor",
                "001",
                "+1234567890",
                "testSupervisor@mail.com",
                null,
                null);
        List<String> memberGroupIdList = new ArrayList<>();
        memberGroupIdList.add("testGroup001");
        List<String> groupMemberIdList = new ArrayList<>();
        Map<String, String> memberIdNameMap = new LinkedHashMap<>();
        LinkedHashMap<String, String> memberNameIdMap = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            User testMember = new User(
                    "testMember00".concat(String.valueOf(i)),
                    "fName ".concat(String.valueOf(i)),
                    "lName ".concat(String.valueOf(i)),
                    "+111111111".concat(String.valueOf(i)),
                    "testMember".concat(String.valueOf(i)).concat("@mail.com"),
                    null,
                    memberGroupIdList);
            groupMemberIdList.add(testMember.getUid());
            memberIdNameMap.put(testMember.getUid(), testMember.getFullName());
            memberNameIdMap.put(testMember.getFullName(), testMember.getUid());
        }
        listViewMatcher = new ListViewMatcher(memberNameIdMap);
        MutableLiveData<Map<String, String>> memberIdNameMapData = new MutableLiveData<>();
        memberIdNameMapData.postValue(memberIdNameMap);

        testSchedule = new Schedule();
        testSchedule.setUid("schedule001");
        testSchedule.setTitle("testSchedule001");
        testSchedule.setEvent("testEvent001");
        testSchedule.setGroupUid("testGroup001");
        testSchedule.setGroupSupervisorUid(testSupervisor.getUid());
        testSchedule.setMemberList(groupMemberIdList);
        testSchedule.setStartTime(timestampNow);
        testSchedule.setEndTime(new Timestamp(timestampNow.getSeconds() + 3600, 0));
        Map<String, UserScheduleResponse> testScheduleMembersMap = new HashMap<>();
        testScheduleMembersMap.put("testMember000", new UserScheduleResponse(Response.ACCEPT, new Timestamp(timestampNow.getSeconds() + 60, 0)));
        testScheduleMembersMap.put("testMember001", new UserScheduleResponse(Response.ACCEPT, new Timestamp(timestampNow.getSeconds() + 120, 0)));
        testScheduleMembersMap.put("testMember002", new UserScheduleResponse(Response.ACCEPT, new Timestamp(timestampNow.getSeconds() + 180, 0)));
        testScheduleMembersMap.put("testMember003", new UserScheduleResponse(Response.ACCEPT, new Timestamp(timestampNow.getSeconds() + 240, 0)));
        testScheduleMembersMap.put("testMember004", new UserScheduleResponse(Response.REJECT, new Timestamp(timestampNow.getSeconds() + 300, 0)));
        testScheduleMembersMap.put("testMember005", new UserScheduleResponse(Response.NEUTRAL, null));
        testSchedule.setUserScheduleResponseMap(testScheduleMembersMap);
        testSchedule.setLocation(new GeoPoint(45.49661075, -73.57853574999999));
        testScheduleData = new MutableLiveData<>(testSchedule);
        testScheduleData.postValue(testSchedule);

        when(mockScheduleReportViewModel.isCurrentUserSupervisor(testSupervisor.getUid())).thenReturn(true);
        when(mockScheduleReportViewModel.getCurrentScheduleData(testSchedule.getUid())).thenReturn(testScheduleData);
        when(mockScheduleReportViewModel.getScheduleMemberNameMap(testSchedule.getMemberList())).thenReturn(memberIdNameMapData);

        Intent testArrivingIntent = new Intent(getApplicationContext(), ScheduleReportActivity.class);
        testArrivingIntent.putExtra("scheduleUid", testSchedule.getUid());
        testArrivingIntent.putExtra("supervisorId", testSupervisor.getUid());

        ActivityScenario<ScheduleReportActivity> activityScenario = ActivityScenario.launch(testArrivingIntent);
        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
            currentActivity = activity;
            activity.setScheduleReportViewModel(mockScheduleReportViewModel);
        });
        activityScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.top_app_bar)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.schedule_report_title), withText(testSchedule.getTitle()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.schedule_report_start_time), withText(String.valueOf(testSchedule.getStartTime().toDate())))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.schedule_report_end_time), withText(String.valueOf(testSchedule.getEndTime().toDate())))).check(matches(isDisplayed()));
        String CONCORDIA_ADDRESS = "1571 Rue Mackay, Montréal, QC H3G 2H6, Canada";
        onView(allOf(withId(R.id.schedule_report_location), withText(CONCORDIA_ADDRESS))).check(matches(isDisplayed()));
        onView(withId(R.id.divider)).check(matches(isDisplayed()));
        onView(withId(R.id.schedule_report_member_list)).check(matches(isDisplayed()));

        verify(mockScheduleReportViewModel, atMostOnce()).getCurrentScheduleData(testSchedule.getUid());
        verify(mockScheduleReportViewModel, atMostOnce()).getScheduleMemberNameMap(testSchedule.getMemberList());
        assertEquals(testSchedule.getMemberList().size(), currentActivity.getScheduleReportMembersAdapter().getCount());
    }

    @Test
    @Ignore("Always fail abnormally on android CI")
    public void scheduleScheduledInOneHourTest() {
        testSchedule.setStartTime(new Timestamp(timestampNow.getSeconds() + 3600, 0));
        testSchedule.setEndTime(new Timestamp(timestampNow.getSeconds() + 7200, 0));
        testScheduleData.postValue(testSchedule);

        listViewMatcher.checkAttendanceMatchOnView();
    }

    @Test
    @Ignore("Always fail abnormally on android CI")
    public void scheduleOnGoingTest() {
        testSchedule.setStartTime(new Timestamp(timestampNow.getSeconds() - 3600, 0));
        testSchedule.setEndTime(new Timestamp(timestampNow.getSeconds() + 3600, 0));
        testSchedule.getUserScheduleResponseMap().put("testMember000",
                new UserScheduleResponse(Response.ACCEPT,
                        new Timestamp(timestampNow.getSeconds() - 7200, 0),
                        new Attendance(true, new Timestamp(timestampNow.getSeconds() - 3600, 0))));
        testSchedule.getUserScheduleResponseMap().put("testMember001",
                new UserScheduleResponse(Response.ACCEPT,
                        new Timestamp(timestampNow.getSeconds() - 7200, 0),
                        new Attendance(false)));
        testScheduleData.postValue(testSchedule);

        onView(withText("Schedule is ongoing and attendance results are subject to change"))
                .inRoot(withDecorView(not(is(currentActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        listViewMatcher.checkAttendanceMatchOnView();
    }

    @Test
    @Ignore("Always fail abnormally on android CI")
    public void scheduleCompletedTest() {
        testSchedule.setStartTime(new Timestamp(timestampNow.getSeconds() - 7200, 0));
        testSchedule.setEndTime(new Timestamp(timestampNow.getSeconds() - 3600, 0));
        testSchedule.getUserScheduleResponseMap().put("testMember000",
                new UserScheduleResponse(Response.ACCEPT,
                        new Timestamp(timestampNow.getSeconds() - 8000, 0),
                        new Attendance(true, new Timestamp(timestampNow.getSeconds() - 7200, 0))));
        testSchedule.getUserScheduleResponseMap().put("testMember001",
                new UserScheduleResponse(Response.ACCEPT,
                        new Timestamp(timestampNow.getSeconds() - 8000, 0),
                        new Attendance(false)));
        testScheduleData.postValue(testSchedule);

        listViewMatcher.checkAttendanceMatchOnView();
    }

    private static class ListViewMatcher {
        private final Map<String, String> memberNameIdMap;

        private ListViewMatcher(Map<String, String> memberNameIdMap) {
            this.memberNameIdMap = memberNameIdMap;
        }

        private void checkAttendanceMatchOnView() {
            for (int i = 0; i < testSchedule.getUserScheduleResponseMap().size(); i++) {
                onView(atPositionOnView(i)).check(matches(isDisplayed()));
            }
        }

        private Matcher<View> atPositionOnView(int position) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;

                @Override
                public void describeTo(Description description) {
                    description.appendText("with id: " + this.resources.getResourceName(R.id.schedule_report_member_list));
                }

                @Override
                protected boolean matchesSafely(View view) {
                    this.resources = view.getResources();
                    ListView listView = currentActivity.findViewById(R.id.schedule_report_member_list);

                    TextView attendanceResultTextView = listView.getChildAt(position).findViewById(R.id.schedule_report_member_attendance);
                    String memberAttendanceResult = attendanceResultTextView.getText().toString();

                    TextView memberNameTextView = listView.getChildAt(position).findViewById(R.id.schedule_report_member_name);
                    String memberId = memberNameIdMap.get(memberNameTextView.getText().toString());

                    String memberExpectedAttendanceResult = testSchedule.scheduleMemberResponseAttendanceMap(memberId).get("responseText").toString();

                    if (testSchedule.scheduleMemberResponseAttendanceMap(memberId).containsKey("attendanceTime")) {
                        TextView attendanceTimeTextView = listView.getChildAt(position).findViewById(R.id.schedule_report_member_attendance_time);
                        String memberAttendanceTime = attendanceTimeTextView.getText().toString();
                        String memberExpectedAttendanceTime = testSchedule.scheduleMemberResponseAttendanceMap(memberId).get("attendanceTime").toString();
                        return view == listView
                                && memberAttendanceResult.equals(memberExpectedAttendanceResult)
                                && memberAttendanceTime.equals(memberExpectedAttendanceTime);
                    } else
                        return view == listView && memberAttendanceResult.equals(memberExpectedAttendanceResult);
                }
            };
        }
    }
}