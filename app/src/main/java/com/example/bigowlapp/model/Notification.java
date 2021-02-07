package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification extends Model {

    public enum Type {
        NONE(Notification.class),
        SUPERVISION_REQUEST(SupervisionRequest.class),
        SCHEDULE_REQUEST(ScheduleRequest.class);

        public final Class<? extends Notification> typeClass;

        Type(Class<? extends Notification> typeClass) {
            this.typeClass = typeClass;
        }
    }

    private Type type;
    private Timestamp time;

    public Notification() {
        super();
        this.type = Type.NONE;
    }

    public Notification(Type type) {
        super();
        this.type = type;
    }

    public Notification(String uId, Type type) {
        super(uId);
        this.type = type;
    }

    public Notification(String uId, Type type, Timestamp time) {
        super(uId);
        this.type = type;
        this.time = time;
    }

    public Type getType() {
        return type;
    }

    private void setType(Type type) {
        this.type = type;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
