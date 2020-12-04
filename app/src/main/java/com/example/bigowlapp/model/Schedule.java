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
    private Map<String, UserResponse> members;
    private String title;
    private String event;
    private List<String> memberList;

    public static class UserResponse {
        private Response response;
        private Timestamp responseTime;
        private boolean exists;


        public UserResponse() {
            exists = true;
        }

        public UserResponse(Response response, Timestamp responseTime) {
            this.response = response;
            this.responseTime = responseTime;
            exists = true;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
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

    public Schedule() {
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