package com.example.bigowlapp.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ScheduleRequest extends Notification {
    private String senderUid;
    private String groupUid;
    private UserScheduleResponse senderResponse;

    public ScheduleRequest() {
        super(Type.SCHEDULE_REQUEST);
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

    public UserScheduleResponse getSenderResponse() {
        return senderResponse;
    }

    public void setSenderResponse(UserScheduleResponse senderResponse) {
        this.senderResponse = senderResponse;
    }

    public static class Field extends Notification.Field {
        public static final String SENDER_UID = "senderUid";
        public static final String GROUP_UID = "groupUid";
        public static final String SENDER_RESPONSE = "senderResponse";
    }
}
