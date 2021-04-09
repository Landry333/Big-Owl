package com.example.bigowlapp.model;

import android.graphics.Color;

import com.example.bigowlapp.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@IgnoreExtraProperties
public class Schedule extends Model {

    /**
     * This represent the maximum time a schedule can be tracked before its attendance results are
     * considered final. After this much time, schedule attendance is not updated.
     */
    public static final long MAX_TRACKING_TIME_MILLIS = 30L * Constants.MINUTE_TO_MILLIS;

    private String title;
    private String event;
    private String groupUid;
    private String groupSupervisorUid;
    private List<String> memberList;
    private Timestamp startTime;
    private Timestamp endTime;
    private GeoPoint location;
    private Map<String, UserScheduleResponse> userScheduleResponseMap;

    public Schedule() {
        super();
    }

    public static Schedule getPrototypeSchedule() {
        Schedule schedule = new Schedule();
        schedule.title = "";
        schedule.event = "";

        Calendar currentTime = Calendar.getInstance();
        Calendar oneHourLaterTime = Calendar.getInstance();
        oneHourLaterTime.add(Calendar.HOUR_OF_DAY, 1);

        schedule.startTime = new Timestamp(currentTime.getTime());
        schedule.endTime = new Timestamp(oneHourLaterTime.getTime());
        return schedule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public String getGroupSupervisorUid() {
        return groupSupervisorUid;
    }

    public void setGroupSupervisorUid(String groupSupervisorUid) {
        this.groupSupervisorUid = groupSupervisorUid;
    }

    public List<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Map<String, UserScheduleResponse> getUserScheduleResponseMap() {
        return userScheduleResponseMap;
    }

    public void setUserScheduleResponseMap(Map<String, UserScheduleResponse> userScheduleResponseMap) {
        this.userScheduleResponseMap = userScheduleResponseMap;
    }

    @Exclude
    public Map<String, Object> scheduleMemberResponseAttendanceMap(String memberId) {
        UserScheduleResponse userScheduleResponse = this.userScheduleResponseMap.get(memberId);
        Response memberResponse = Objects.requireNonNull(userScheduleResponse).getResponse();
        Attendance memberAttendance = userScheduleResponse.getAttendance();
        String responseText = "responseText";
        String responseColor = "responseColor";
        Map<String, Object> map = new HashMap<>();

        if (memberAttendance.didAttend()) {
            map.put(responseText, "ATTENDED");
            map.put(responseColor, Color.GREEN);
            map.put("attendanceTime", memberAttendance.getAuthenticationTime().toDate().toString());
        } else if (memberResponse == Response.REJECT) {
            map.put(responseText, "REJECTED");
            map.put(responseColor, Color.RED);
        } else if (memberResponse == Response.NEUTRAL) {
            map.put(responseText, "NO RESPONSE");
            map.put(responseColor, Color.GRAY);
        } else {
            switch (scheduleCurrentState()) {
                case SCHEDULED:
                    map.put(responseText, "ACCEPTED");
                    map.put(responseColor, Color.GREEN);
                    break;
                case ON_GOING:
                    map.put(responseText, "PENDING");
                    map.put(responseColor, Color.BLUE);
                    break;
                case COMPLETED:
                    map.put(responseText, "MISSED");
                    map.put(responseColor, Color.RED);
                    break;
                default:
                    map.put(responseText, "ERROR");
                    map.put(responseColor, Color.RED);
                    break;
            }
        }
        return map;
    }

    @Exclude
    public Status scheduleCurrentState() {
        final int THIRTY_MINUTES = 1800;
        Timestamp timeNow = Timestamp.now();
        if (timeNow.compareTo(startTime) < 0) {
            return Status.SCHEDULED;
        } else if (timeNow.getSeconds() <= (startTime.getSeconds() + THIRTY_MINUTES)) {
            return Status.ON_GOING;
        } else {
            return Status.COMPLETED;
        }
    }

    public enum Status {
        SCHEDULED, ON_GOING, COMPLETED
    }

    public static class Field {
        public static final String TITLE = "title";
        public static final String EVENT = "event";
        public static final String GROUP_UID = "groupUid";
        public static final String GROUP_SUPERVISOR_UID = "groupSupervisorUid";
        public static final String MEMBER_LIST = "memberList";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String LOCATION = "location";
        public static final String USER_SCHEDULE_RESPONSE_MAP = "userScheduleResponseMap";

        private Field() {
            // constants class should not be instantiated
        }
    }
}