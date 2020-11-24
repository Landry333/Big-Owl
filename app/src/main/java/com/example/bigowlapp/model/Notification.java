package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification {

    @DocumentId
    private String uId;
    private String type;
    private Timestamp time;
    protected String receiverUId;

    public Notification() {
    }

    public Notification(String uId, String type, Timestamp time, String receiverUId) {
        this.uId = uId;
        this.type = type;
        this.time = time;
        this.receiverUId = receiverUId;
    }

    public String getuId() { return uId; }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(String receiverUId) {
        this.receiverUId = receiverUId;
    }
}
