package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification {

    @DocumentId
    private String uId;
    private String senderUId;
    private String receiverUId;
    private String groupUId;
    private Boolean read;
    private String type;
    private Timestamp timeRead;
    private Timestamp timeSend;

    public Notification() {
    }

    public Notification(String type) {
        this.type = type;
    }

    public Notification(String uId, String type) {
        this.uId = uId;
        this.type = type;
    }

    public Notification(String uId, String type, Timestamp timeRead) {
        this.uId = uId;
        this.type = type;
        this.timeRead = timeRead;
    }

    public Notification(String uId, String senderUId, String receiverUId, String groupUId, Boolean read, String type, Timestamp timeRead, Timestamp timeSend) {
        this.uId = uId;
        this.senderUId = senderUId;
        this.receiverUId = receiverUId;
        this.groupUId = groupUId;
        this.read = read;
        this.type = type;
        this.timeRead = timeRead;
        this.timeSend = timeSend;
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

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Timestamp getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Timestamp timeSend) {
        this.timeSend = timeSend;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTimeRead() {
        return timeRead;
    }

    public void setTimeRead(Timestamp timeRead) {
        this.timeRead = timeRead;
    }
}
