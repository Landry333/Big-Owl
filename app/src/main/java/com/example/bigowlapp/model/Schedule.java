package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Schedule {

    @DocumentId
    private String uId;
    private String groupUId;
    private Timestamp startTime;
    private Timestamp endTime;
    private GeoPoint location;
    private String groupSupervisorUId;
    private Map<String, UserScheduleResponse> userScheduleResponseMap;
    private String title;
    private String event;
    private List<String> memberList;

    public Map<String, UserScheduleResponse> getMembers() {
        return userScheduleResponseMap;
    }

    public void setMembers(Map<String, UserScheduleResponse> userScheduleResponseMap) {
        this.userScheduleResponseMap = userScheduleResponseMap;
    }

    public String getGroupSupervisorUId() {
        return groupSupervisorUId;
    }

    public void setGroupSupervisorUId(String groupSupervisorUId) {
        this.groupSupervisorUId = groupSupervisorUId;
    }

    public Schedule() {
        // Necessary for Firbase data mapping to model object
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getGroupUId() {
        return groupUId;
    }

    public void setGroupUId(String groupUId) {
        this.groupUId = groupUId;
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

    public List<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }
}