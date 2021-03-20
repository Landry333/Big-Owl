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

    private boolean authAttemptedUserMobileNumber = false;  // if authentication has been attempted at first using the UserMobileNumber
    private boolean authAttemptedPhoneUid = false;  // if authentication has been attempted secondly using the PhoneUid
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

    public boolean isAuthAttemptedUserMobileNumber() {
        return authAttemptedUserMobileNumber;
    }

    public void setAuthAttemptedUserMobileNumber(boolean authAttemptedUserMobileNumber) {
        this.authAttemptedUserMobileNumber = authAttemptedUserMobileNumber;
    }

    public boolean isAuthAttemptedPhoneUid() {
        return authAttemptedPhoneUid;
    }

    public void setAuthAttemptedPhoneUid(boolean authAttemptedPhoneUid) {
        this.authAttemptedPhoneUid = authAttemptedPhoneUid;
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
        return authenticated;
        // authenticated begins only for CORRECT_LOCATION,
        // so authenticated is enough to determine attendance
    }

}