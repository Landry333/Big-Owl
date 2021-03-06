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
import com.google.i18n.phonenumbers.NumberParseException;

import java.util.Calendar;

public class SupervisorSmsListener extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String scheduleId;


    public void onReceive(Context context, Intent receiverIntent) {
        if (receiverIntent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = receiverIntent.getExtras();
            if (bundle != null) {
                // Get PDUs and format of the key PDU
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");
                if (pdus.length == 0) {
                    return;
                }
                // Get message from PDU
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    sb.append(messages[i].getMessageBody());
                }
                String smsSenderNum = messages[0].getOriginatingAddress();
                scheduleId = sb.toString();
                Timestamp currentTime = new Timestamp(Calendar.getInstance().getTime());
                String formattedSmsSenderNum;
                try {
                    formattedSmsSenderNum = new PhoneNumberFormatter(context).formatNumber(smsSenderNum);
                } catch (NumberParseException e) {
                    Log.e("BigOwl", Log.getStackTraceString(e));
                    Toast.makeText(context, "FAILED to format phone number for next schedule authentication. Process failed", Toast.LENGTH_LONG).show();
                    return;
                }
                authenticateIfSenderIsSupervisor(context, currentTime, formattedSmsSenderNum);
            }
        }
    }

    private void authenticateIfSenderIsSupervisor(Context context, Timestamp currentTime, String formattedSmsSenderNum) {
        IsSmsSenderACurrentEventSupervisor isSmsSenderACurrentEventSupervisor = new IsSmsSenderACurrentEventSupervisor();
        isSmsSenderACurrentEventSupervisor.check(formattedSmsSenderNum)
                .addOnSuccessListener(schedulesList -> {
                    for (Schedule schedule : schedulesList) {
                        if (Math.abs(schedule.getStartTime().toDate().getTime() - currentTime.toDate().getTime()) < Schedule.MAX_TRACKING_TIME_MILLIS) {
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
                });
    }
}
