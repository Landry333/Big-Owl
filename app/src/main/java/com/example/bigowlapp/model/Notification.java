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

    public Type getType() {
        return type;
    }

    // This private setter is necessary for Firebase data to model mapping
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
