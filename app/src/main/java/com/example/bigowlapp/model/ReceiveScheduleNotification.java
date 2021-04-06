package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ReceiveScheduleNotification extends Notification {

    private String senderUid;
    private String groupUid;
    private Timestamp timeResponse;
    private String scheduleUid;
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ReceiveScheduleNotification() {
        super(Type.SCHEDULE_NOTIFICATION);
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public Timestamp getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(Timestamp timeResponse) {
        this.timeResponse = timeResponse;
    }

    public String getScheduleUid() {
        return scheduleUid;
    }

    public void setScheduleUid(String scheduleUid) {
        this.scheduleUid = scheduleUid;
    }

    public static class Field extends Notification.Field {
        public static final String SENDER_UID = "senderUid";
        public static final String GROUP_UID = "groupUid";
        public static final String RESPONSE = "response";
        public static final String TIME_RESPONSE = "timeResponse";

        private Field() {
            // constants class should not be instantiated
        }
    }
}
