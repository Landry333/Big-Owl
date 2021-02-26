package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


@IgnoreExtraProperties
public class Schedule extends Model {

    private String title;
    private String event;
    private String groupUid;
    private String groupSupervisorUid;
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

    public String toString(){
        return ("\n\nTitle: "+ getTitle() + "\n\nMembers: " + getMemberList() + "\n\nStart Time: " + getStartTime() + "\n\nEnd Time: " + getEndTime() + "\n\nLocation: " + getLocation());
    }
}