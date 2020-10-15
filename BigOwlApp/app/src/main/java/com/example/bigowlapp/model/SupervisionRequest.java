package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;

public class SupervisionRequest extends Notification {

    private final String TYPE = "supervisionRequest";
    private enum Response {
        ACCEPT,
        REJECT,
        NEUTRAL
    }

    private String senderUId;
    private String receiverUId;
    private String groupUId;
    private Response response;
    private Timestamp timeSent;
    private Timestamp timeResponse;

    SupervisionRequest(){
        super();
    }

    SupervisionRequest(String uId, String type){
        super(uId, type);
    }

    public SupervisionRequest(String uId, String type, Timestamp time, String senderUId,
                              String receiverUId, String groupUId, Response response,
                              Timestamp timeSent, Timestamp timeResponse) {
        super(uId, type, time);
        this.senderUId = senderUId;
        this.receiverUId = receiverUId;
        this.groupUId = groupUId;
        this.response = response;
        this.timeSent = timeSent;
        this.timeResponse = timeResponse;
    }

    public String getTYPE() {
        return TYPE;
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
}
