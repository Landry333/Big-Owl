package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;

public class UserScheduleResponse {
    private Response response;
    private Timestamp responseTime;
    private Attendance attendance;

    public UserScheduleResponse() {
        this.attendance = new Attendance();
    }

    public UserScheduleResponse(Response response, Timestamp responseTime) {
        this();
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

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
}
