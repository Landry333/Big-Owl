package com.example.bigowlapp.utils;

import android.telephony.SmsManager;


public class SmsSender {
    public void sendSMS(String smsNumber, String smsMessage) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber, null, smsMessage, null, null);
    }
}
