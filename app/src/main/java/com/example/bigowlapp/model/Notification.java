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

    /**
     * The type is used to differentiate notifications when storing/retrieving them from the db
     */
    protected Type type;

    /**
     * The date/time the notification was created
     */
    protected Timestamp time;

    /**
     * The database uid of the user who will receive this notification
     */
    protected String receiverUid;

    /**
     * The date/time the notification was viewed by its receiver
     */
    protected Timestamp timeRead;

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

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public Timestamp getTimeRead() {
        return timeRead;
    }

    public void setTimeRead(Timestamp timeRead) {
        this.timeRead = timeRead;
    }

    public static class Field {
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String RECEIVER_UID = "receiverUid";
        public static final String TIME_READ = "timeRead";
    }
}
