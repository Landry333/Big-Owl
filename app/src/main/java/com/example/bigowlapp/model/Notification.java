package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification extends Model {

    public enum Type {
        NONE(Notification.class),
        INVALID(NullNotification.class),
        SUPERVISION_REQUEST(SupervisionRequest.class),
        SCHEDULE_REQUEST(ScheduleRequest.class),
        AUTH_BY_PHONE_NUMBER_FAILURE(AuthByPhoneNumberFailure.class);

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
    protected Timestamp creationTime;

    /**
     * The database uid of the user who will receive this notification
     */
    protected String receiverUid;

    /**
     * The date/time the notification was viewed by its receiver
     */
    protected Timestamp timeRead;

    protected boolean used = false;

    public Notification() {
        this(Type.NONE);
    }

    public Notification(Type type) {
        super();
        this.type = type;
    }

    @Exclude
    public static Notification getNotificationSafe(Notification notification) {
        return notification == null || notification.type == null ? new NullNotification() : notification;
    }

    public Type getType() {
        return type;
    }

    // This private setter is necessary for Firebase data to model mapping
    private void setType(Type type) {
        this.type = type;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Exclude
    public boolean isValid() {
        return true;
    }

    public long timeSinceCreationMillis() {
        if (creationTime == null) {
            return -1L;
        }

        return Timestamp.now().toDate().getTime() - this.getCreationTime().toDate().getTime();
    }

    public static class Field {
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String RECEIVER_UID = "receiverUid";
        public static final String TIME_READ = "timeRead";

        protected Field() {
            // constants class should not be instantiated
        }
    }
}
