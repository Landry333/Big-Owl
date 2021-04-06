package com.example.bigowlapp.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AuthByPhoneNumberFailure extends Notification {

    private String senderUid;
    private String groupUid;
    private String scheduleId;
    private String senderPhoneNum;

    public AuthByPhoneNumberFailure() {
        super(Type.AUTH_BY_PHONE_NUMBER_FAILURE);
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

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSenderPhoneNum() {
        return senderPhoneNum;
    }

    public void setSenderPhoneNum(String senderPhoneNum) {
        this.senderPhoneNum = senderPhoneNum;
    }

    public static class Field extends Notification.Field {
        public static final String SENDER_UID = "senderUid";
        public static final String GROUP_UID = "groupUid";
        public static final String SCHEDULE_ID = "scheduleId";
        public static final String SENDER_PHONE_NUM = "senderPhoneNum";

        private Field() {
            // constants class should not be instantiated
        }
    }

}
