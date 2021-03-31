package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SupervisionRequest extends Notification {

    private String senderUid;
    private String groupUid;
    private Response response;
    private Timestamp timeResponse;

    public SupervisionRequest() {
        super(Type.SUPERVISION_REQUEST);
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Timestamp getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(Timestamp timeResponse) {
        this.timeResponse = timeResponse;
    }

    public enum Response {
        ACCEPT,
        REJECT,
        NEUTRAL
    }

    @Override
    public String toString() {
        return "SupervisionRequest{" +
                "senderUId='" + senderUid + '\'' +
                ", receiverUId='" + receiverUid + '\'' +
                ", groupUId='" + groupUid + '\'' +
                ", response=" + response +
                ", timeResponse=" + timeResponse +
                '}';
    }

    public static class Field extends Notification.Field {
        public static final String SENDER_UID = "senderUid";
        public static final String GROUP_UID = "groupUid";
        public static final String RESPONSE = "response";
        public static final String TIME_RESPONSE = "timeResponse";

        private Field() {
            super();
            // constants class should not be instantiated
        }
    }
}
