package com.example.bigowlapp.model;

import com.example.bigowlapp.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ScheduleRequest extends Notification {
    private String senderUId;
    private String receiverUId;
    private String groupUId;
    private Timestamp timeRead;
    private UserScheduleResponse senderResponse;

    public ScheduleRequest() {
        super();
        this.setType(Constants.SCHEDULE_TYPE);
    }

    public String getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(String senderUId) {
        this.senderUId = senderUId;
    }

    public String getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(String receiverUId) {
        this.receiverUId = receiverUId;
    }

    public String getGroupUId() {
        return groupUId;
    }

    public void setGroupUId(String groupUId) {
        this.groupUId = groupUId;
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
