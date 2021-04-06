package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification extends Model {

    public enum Type {
        NONE(Notification.class, ""),
        INVALID(NullNotification.class, "Invalid"),
        SUPERVISION_REQUEST(SupervisionRequest.class, "Supervise request"),
        SCHEDULE_REQUEST(ScheduleRequest.class, "Schedule request"),
        AUTH_BY_PHONE_NUMBER_FAILURE(AuthByPhoneNumberFailure.class, "Phone number failed"),
        SMS_INVITATION_REQUEST(SmsInvitationRequest.class, "SMS invitation");

        public final Class<? extends Notification> typeClass;
        public final String title;

        Type(Class<? extends Notification> typeClass, String title) {
            this.typeClass = typeClass;
            this.title = title;
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

    /**
     * Marks a notification as used to verify the user received and/or viewed it
     */
    protected boolean used;

    /**
     * Marks a notification as used to verify the user received and/or viewed it
     */
    protected String message;

    public Notification() {
        this(Type.NONE);
    }

    public Notification(Type type) {
        super();
        this.type = type;
        this.used = false;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    @Exclude
    public String getTitle(){
        if(this.getType() != null) {
            return this.getType().title;
        }
        return "";
    }

    public static class Field {
        public static final String TYPE = "type";
        public static final String CREATION_TIME = "creationTime";
        public static final String RECEIVER_UID = "receiverUid";
        public static final String TIME_READ = "timeRead";
        public static final String USED = "used";

        protected Field() {
            // constants class should not be instantiated
        }
    }
}
