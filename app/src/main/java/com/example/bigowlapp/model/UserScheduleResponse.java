package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class UserScheduleResponse {
    private Response response;
    private Timestamp responseTime;
    private Attendance attendance;

    public class Attendance {
        private boolean authenticated =false;
        private boolean authAttempted_Method1 =false;  // Authentication with first method
        private boolean authAttempted_Method2 =false;  // Authentication with second method
        private boolean scheduleLocated =false;
        private String deviceIdNumber;

        public Attendance() {
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }

        public boolean isAuthAttempted_Method1() {
            return authAttempted_Method1;
        }

        public void setAuthAttempted_Method1(boolean authAttempted_Method1) {
            this.authAttempted_Method1 = authAttempted_Method1;
        }
        public boolean isAuthAttempted_Method2() {
            return authAttempted_Method2;
        }

        public void setAuthAttempted_Method2(boolean authAttempted_Method2) {
            this.authAttempted_Method2 = authAttempted_Method2;
        }

        public boolean isScheduleLocated() {
            return scheduleLocated;
        }

        public void setScheduleLocated(boolean scheduleLocated) {
            this.scheduleLocated = scheduleLocated;
        }

        public String getDeviceIdNumber() {
            return deviceIdNumber;
        }

        public void setDeviceIdNumber(String deviceIdNumber) {
            this.deviceIdNumber = deviceIdNumber;
        }

        @Exclude
        public boolean didAttend(){
            return authenticated&& isScheduleLocated();

        }
    }

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
