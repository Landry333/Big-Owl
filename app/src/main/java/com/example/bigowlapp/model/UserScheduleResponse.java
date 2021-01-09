package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;

public class UserScheduleResponse {
    private Response response;
    private Timestamp responseTime;

    public UserScheduleResponse(Response response, Timestamp responseTime) {
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
