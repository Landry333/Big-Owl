package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Attendance {
    public enum LocatedStatus {
        NOT_DETECTED,
        CORRECT_LOCATION,
        WRONG_LOCATION
    }

    private boolean authenticated;
    /**
     * if authentication has been attempted at first using the UserMobileNumber
     */
    private boolean attemptedAuthByUserMobileNumber;
    /**
     * if authentication has been attempted secondly using the PhoneUid
     */
    private boolean attemptedAuthByAppUid;

    private LocatedStatus scheduleLocated;
    private String appInstanceId;
    private Timestamp authenticationTime;

    public Attendance() {
        // public no-argument constructor necessary for Firebase data mapping
        authenticated = false;
        attemptedAuthByUserMobileNumber = false;
        attemptedAuthByAppUid = false;
        scheduleLocated = LocatedStatus.NOT_DETECTED;
    }

    public Attendance(boolean authenticated) {
        this();
        this.authenticated = authenticated;
    }

    public Attendance(boolean authenticated, Timestamp authenticationTime) {
        this();
        this.authenticated = authenticated;
        this.authenticationTime = authenticationTime;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public LocatedStatus getScheduleLocated() {
        return scheduleLocated;
    }

    public void setScheduleLocated(LocatedStatus scheduleLocated) {
        this.scheduleLocated = scheduleLocated;
    }

    public String getAppInstanceId() {
        return appInstanceId;
    }

    public void setAppInstanceId(String appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    public Timestamp getAuthenticationTime() {
        return authenticationTime;
    }

    public void setAuthenticationTime(Timestamp authenticationTime) {
        this.authenticationTime = authenticationTime;
    }

    public boolean isAttemptedAuthByUserMobileNumber() {
        return attemptedAuthByUserMobileNumber;
    }

    public void setAttemptedAuthByUserMobileNumber(boolean attemptedAuthByUserMobileNumber) {
        this.attemptedAuthByUserMobileNumber = attemptedAuthByUserMobileNumber;
    }

    public boolean isAttemptedAuthByAppUid() {
        return attemptedAuthByAppUid;
    }

    public void setAttemptedAuthByAppUid(boolean attemptedAuthByAppUid) {
        this.attemptedAuthByAppUid = attemptedAuthByAppUid;
    }

    @Exclude
    public boolean didAttend() {
        return authenticated;
        // authenticated begins only for CORRECT_LOCATION,
        // so authenticated is enough to determine attendance
    }

}

