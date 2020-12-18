package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification extends Model {

    private String type;
    private Timestamp time;

    public Notification() {
        super();
    }

    public Notification(String type) {
        super();
        this.type = type;
    }

    public Notification(String uId, String type) {
        super(uId);
        this.type = type;
    }

    public Notification(String uId, String type, Timestamp time) {
        super(uId);
        this.type = type;
        this.time = time;
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
}
