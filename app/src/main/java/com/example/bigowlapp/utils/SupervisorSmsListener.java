package com.example.bigowlapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.google.firebase.Timestamp;

import java.util.Calendar;

public class SupervisorSmsListener extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final int THIRTY_MINUTES = 1800;
    private String scheduleId;

    IsSmsSenderACurrentEventSupervisor isSmsSenderACurrentEventSupervisor = new IsSmsSenderACurrentEventSupervisor();

    public void onReceive(Context context, Intent receiverIntent) {
        if (receiverIntent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = receiverIntent.getExtras();
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
                String smsSender = messages[0].getOriginatingAddress().substring(2);
                scheduleId = sb.toString();
                Timestamp currentTime = new Timestamp(Calendar.getInstance().getTime());
                isSmsSenderACurrentEventSupervisor.check(smsSender)
                        .addOnSuccessListener(schedulesList -> {
                            for (Schedule schedule : schedulesList) {
                                if (Math.abs(schedule.getStartTime().getSeconds() - currentTime.getSeconds()) < THIRTY_MINUTES) {
                                    if (scheduleId.equalsIgnoreCase(schedule.getUid())) {
                                        try {
                                            AuthenticatorByAppInstanceId authenticatorByAppInstanceId = new AuthenticatorByAppInstanceId(context);
                                            authenticatorByAppInstanceId.authenticate(scheduleId);
                                        } catch (Exception e) {
                                            Log.e("exception", "the message", e);
                                        }
                                    }
                                    break;
                                }
                            }
                        })
                        .addOnFailureListener(error -> {
                            Toast.makeText(context, "FAILURE for checking if the SMS was from supervisor", Toast.LENGTH_SHORT).show();
                        });

            }
        }
    }

}
