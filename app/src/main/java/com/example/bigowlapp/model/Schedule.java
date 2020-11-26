package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.List;

@IgnoreExtraProperties
public class Schedule {

    @DocumentId
    private String uId;
    private String title;
    private List<String> listOfUserIds;
    private String groupId;
    private String event;
    private Timestamp startTime;
    private Timestamp endTime;
    private GeoPoint location;

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
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getListOfUserIds() {
        return listOfUserIds;
    }

    public void setListOfUserIds(List<String> listOfUserIds) {
        this.listOfUserIds = listOfUserIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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
}
