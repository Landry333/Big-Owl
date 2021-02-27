package com.example.bigowlapp.model;

import com.google.firebase.firestore.Exclude;

public class Attendance {
    public enum LocatedStatus {
        NOT_DETECTED,
        CORRECT_LOCATION,
        WRONG_LOCATION
    }

    private boolean authenticated = false;
    private boolean authAttemptedMethod1 = false;  // Authentication with first method
    private boolean authAttemptedMethod2 = false;  // Authentication with second method
    private LocatedStatus scheduleLocated = LocatedStatus.NOT_DETECTED;
    private String deviceIdNumber;

    public Attendance() {
        // public no-argument constructor necessary for Firebase data mapping
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthAttemptedMethod1() {
        return authAttemptedMethod1;
    }

    public void setAuthAttemptedMethod1(boolean authAttemptedMethod1) {
        this.authAttemptedMethod1 = authAttemptedMethod1;
    }

    public boolean isAuthAttemptedMethod2() {
        return authAttemptedMethod2;
    }

    public void setAuthAttemptedMethod2(boolean authAttemptedMethod2) {
        this.authAttemptedMethod2 = authAttemptedMethod2;
    }

    public LocatedStatus getScheduleLocated() {
        return scheduleLocated;
    }

    public void setScheduleLocated(LocatedStatus scheduleLocated) {
        this.scheduleLocated = scheduleLocated;
    }

    public String getDeviceIdNumber() {
        return deviceIdNumber;
    }

    public void setDeviceIdNumber(String deviceIdNumber) {
        this.deviceIdNumber = deviceIdNumber;
    }

    @Exclude
    public boolean didAttend() {
        return authenticated && scheduleLocated == LocatedStatus.CORRECT_LOCATION;

    }
}