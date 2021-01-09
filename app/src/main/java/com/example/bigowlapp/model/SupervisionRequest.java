package com.example.bigowlapp.model;

import com.example.bigowlapp.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SupervisionRequest extends Notification implements Constants {

    private String senderUId;
    private String receiverUId;
    private String groupUId;
    private Response response;
    // TODO: remove timeSent,as it is redundant since 'time' is in base Notification class
    private Timestamp timeSent;
    private Timestamp timeResponse;

    public SupervisionRequest() {
        super();
        this.setType(Constants.SUPERVISION_TYPE);
    }

    public SupervisionRequest(String uId) {
        super(uId, SUPERVISION_TYPE);
    }

    public SupervisionRequest(String uId, Timestamp time, String senderUId,
                              String receiverUId, String groupUId, Response response,
                              Timestamp timeSent, Timestamp timeResponse) {
        super(uId, SUPERVISION_TYPE, time);
        this.senderUId = senderUId;
        this.receiverUId = receiverUId;
        this.groupUId = groupUId;
        this.response = response;
        this.timeSent = timeSent;
        this.timeResponse = timeResponse;
    }

    public String getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(String senderUId) {
        this.senderUId = senderUId;
    }

    public String getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(String receiverUId) {
        this.receiverUId = receiverUId;
    }

    public String getGroupUId() {
        return groupUId;
    }

    public void setGroupUId(String groupUId) {
        this.groupUId = groupUId;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Timestamp getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Timestamp timeSent) {
        this.timeSent = timeSent;
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
