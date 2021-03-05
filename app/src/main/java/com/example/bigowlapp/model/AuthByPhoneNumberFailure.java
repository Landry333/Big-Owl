package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AuthByPhoneNumberFailure extends Notification {

    private String senderUid;
    private String receiverUid;
    private String groupUid;
    private Timestamp timeRead;
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

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public Timestamp getTimeRead() {
        return timeRead;
    }

    public void setTimeRead(Timestamp timeRead) {
        this.timeRead = timeRead;
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

}
