package com.example.bigowlapp.utils;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsSender {


    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public SmsSender() {

    }

    public void sendSMS(Context context, String smsNumber, String smsMessage) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber, null, smsMessage, null, null);
    }

}

