package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Attendance {
    public enum LocatedStatus {
        NOT_DETECTED,
        CORRECT_LOCATION,
        WRONG_LOCATION
    }

    private boolean authenticated = false;
    private boolean attemptedAuthByUserMobileNumber = false;  // if authentication has been attempted at first using the UserMobileNumber
    private boolean attemptedAuthByPhoneUid = false;  // if authentication has been attempted secondly using the PhoneUid
    private LocatedStatus scheduleLocated = LocatedStatus.NOT_DETECTED;
    private String deviceIdNumber;
    private Timestamp authenticationTime;

    public Attendance() {
        // public no-argument constructor necessary for Firebase data mapping
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean attemptedAuthByUserMobileNumber() {
        return attemptedAuthByUserMobileNumber;
    }

    public void setAttemptedAuthByUserMobileNumber(boolean authAttemptedUserMobileNumber) {
        this.attemptedAuthByUserMobileNumber = authAttemptedUserMobileNumber;
    }

    public boolean attemptedAuthByPhoneUid() {
        return attemptedAuthByPhoneUid;
    }

    public void setAttemptedAuthByPhoneUid(boolean authAttemptedPhoneUid) {
        this.attemptedAuthByPhoneUid = authAttemptedPhoneUid;
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

    public Timestamp getAuthenticationTime() {
        return authenticationTime;
    }

    public void setAuthenticationTime(Timestamp authenticationTime) {
        this.authenticationTime = authenticationTime;
    }


    @Exclude
    public boolean didAttend() {
        return authenticated && scheduleLocated == LocatedStatus.CORRECT_LOCATION;

    }
}

