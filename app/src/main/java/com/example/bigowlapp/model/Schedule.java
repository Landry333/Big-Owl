package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


@IgnoreExtraProperties
public class Schedule extends Model {

    private String title;
    private String event;
    private String groupUId;
    private String groupSupervisorUId;
    private List<String> memberList;
    private Timestamp startTime;
    private Timestamp endTime;
    private GeoPoint location;
    private Map<String, UserScheduleResponse> userScheduleResponseMap;

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

    public Schedule() {
        super();
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

    public String getGroupUId() {
        return groupUId;
    }

    public void setGroupUId(String groupUId) {
        this.groupUId = groupUId;
    }

    public String getGroupSupervisorUId() {
        return groupSupervisorUId;
    }

    public void setGroupSupervisorUId(String groupSupervisorUId) {
        this.groupSupervisorUId = groupSupervisorUId;
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

    // TODO: Temp fix since db still has old field name
    @PropertyName("members")
    public Map<String, UserScheduleResponse> getUserScheduleResponseMap() {
        return userScheduleResponseMap;
    }

    // TODO: Temp fix since db still has old field name
    @PropertyName("members")
    public void setUserScheduleResponseMap(Map<String, UserScheduleResponse> userScheduleResponseMap) {
        this.userScheduleResponseMap = userScheduleResponseMap;
    }
}