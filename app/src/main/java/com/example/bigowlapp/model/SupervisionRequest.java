package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SupervisionRequest extends Notification {

    private String senderUid;
    private String receiverUid;
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

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
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
                "senderUId='" + senderUId + '\'' +
                ", receiverUId='" + receiverUId + '\'' +
                ", groupUId='" + groupUId + '\'' +
                ", response=" + response +
                ", timeResponse=" + timeResponse +
                '}';
    }
}
