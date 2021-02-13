package com.example.bigowlapp.model;

import com.example.bigowlapp.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SupervisionRequest extends Notification implements Constants {

    private String senderUid;
    private String receiverUid;
    private String groupUid;
    private Response response;
    private Timestamp timeResponse;

    public SupervisionRequest() {
        super();
        this.setType(Constants.SUPERVISION_TYPE);
    }

    public SupervisionRequest(String uid) {
        super(uid, SUPERVISION_TYPE);
    }

    public SupervisionRequest(String uid, Timestamp time, String senderUid,
                              String receiverUid, String groupUid, Response response,
                              Timestamp timeResponse) {
        super(uid, SUPERVISION_TYPE, time);
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.groupUid = groupUid;
        this.response = response;
        this.timeResponse = timeResponse;
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
}
