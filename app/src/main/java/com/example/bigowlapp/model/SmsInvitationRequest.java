package com.example.bigowlapp.model;

public class SmsInvitationRequest extends Notification {

    private String senderUid;
    private String phoneNumberSent;

    public SmsInvitationRequest() {
        super(Type.SMS_INVITATION_REQUEST);
    }

    public String getSenderUid() {
        return this.senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public void setPhoneNumberSent(String phoneNumberSent){
        this.phoneNumberSent = phoneNumberSent;
    }

}
