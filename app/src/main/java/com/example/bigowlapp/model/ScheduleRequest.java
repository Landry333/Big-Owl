package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ScheduleRequest extends Notification {
    private String senderUid;
    private String receiverUid;
    private String groupUid;
    private Timestamp timeRead;
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

    public UserScheduleResponse getSenderResponse() {
        return senderResponse;
    }

    public void setSenderResponse(UserScheduleResponse senderResponse) {
        this.senderResponse = senderResponse;
    }
}
