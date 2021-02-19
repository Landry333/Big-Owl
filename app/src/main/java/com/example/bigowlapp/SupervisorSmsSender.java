/*package com.example.bigowlapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SupervisorSmsSender extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String smsNumber;
    private String smsMessage;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();

                smsNumber = "+14388313579"; //number of the monitoring user initiating the authentication
                smsMessage = "The authentication code is 123456";//response from supervised user with authentication code

                if(sender.equals(smsNumber)){
                    Toast.makeText(context, "Received BigOwl authentication SMS from: "+sender, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    try{
                        sendSMS(context);
                    }
                    catch (Exception e){
                        // Todo: Add a warning system for failed authentication process
                    }
                }
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }
    private void sendSMS(Context context) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber, null, smsMessage, null, null);
        Toast.makeText(context, "SMS SENT", Toast.LENGTH_SHORT).show();
    }
}*/