package com.example.bigowlapp.model;

import com.google.firebase.firestore.Exclude;

public class Attendance {
    public enum LocatedStatus {
        NOT_DETECTED,
        CORRECT_LOCATION,
        WRONG_LOCATION
    }

    private boolean authenticated = false;
    private boolean authAttempted_Method1 = false;  // Authentication with first method
    private boolean authAttempted_Method2 = false;  // Authentication with second method
    private LocatedStatus scheduleLocated = LocatedStatus.NOT_DETECTED;
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
