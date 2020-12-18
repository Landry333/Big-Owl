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
    private String groupUId;
    private String groupSupervisorUId;
    private List<String> memberList;
    private Timestamp startTime;
    private Timestamp endTime;
    private GeoPoint location;
    //TODO: Change this name to userReponseMap after database change
    private Map<String, UserResponse> members;

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

    public static class UserResponse {
        private Response response;
        private Timestamp responseTime;

        public UserResponse() {
        }

        public UserResponse(Response response, Timestamp responseTime) {
            this.response = response;
            this.responseTime = responseTime;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public Timestamp getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(Timestamp responseTime) {
            this.responseTime = responseTime;
        }
    }

    public Schedule() {
        super();
    }

    public Schedule(String uId, String title, String event, String groupUId,
                    String groupSupervisorUId, List<String> memberList, Timestamp startTime,
                    Timestamp endTime, GeoPoint location, Map<String, UserResponse> members) {
        super(uId);
        this.title = title;
        this.event = event;
        this.groupUId = groupUId;
        this.groupSupervisorUId = groupSupervisorUId;
        this.memberList = memberList;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.members = members;
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


    public Map<String, UserResponse> getMembers() {
        return members;
    }

    public void setMembers(Map<String, UserResponse> members) {
        this.members = members;
    }

    public String getGroupSupervisorUId() {
        return groupSupervisorUId;
    }

    public void setGroupSupervisorUId(String groupSupervisorUId) {
        this.groupSupervisorUId = groupSupervisorUId;
    }
}