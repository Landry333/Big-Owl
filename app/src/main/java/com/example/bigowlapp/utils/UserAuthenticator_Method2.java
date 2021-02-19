package com.example.bigowlapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.bigowlapp.DeviceIDNumberGetter;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserAuthenticator_Method2 extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String smsNumber;
    private String smsMessage;
    private String smsSender;

    RepositoryFacade repositoryFacade =  RepositoryFacade.getInstance();
    IsSmsSenderACurrentEventSupervisor isSmsSenderACurrentEventSupervisor = new IsSmsSenderACurrentEventSupervisor();
    //DeviceIDNumberGetter deviceIDNumberGetter = new DeviceIDNumberGetter();
    //UserAuthenticator_Method1 authenticator_1 = new UserAuthenticator_Method1();

    //private String deviceID = deviceIDNumberGetter.getIDNumber();
    //private String deviceID = authenticator_1.getDeviceID();

    public void onReceive(Context context, Intent intent) {
        AlertDialog.Builder display = new AlertDialog.Builder(context);

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Toast.makeText(context, "RECEIVED NOT EMPTY TEXT SMS", Toast.LENGTH_SHORT).show();
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
                smsSender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                /*if(isSmsSenderACurrentEventSupervisor.check())
                    Toast.makeText(context, "METHOD OK", Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "METHOD FAILED", Toast.LENGTH_SHORT).show();*/

                Timestamp currentTime = new Timestamp(Calendar.getInstance().getTime());

                isSmsSenderACurrentEventSupervisor.check()
                        .addOnSuccessListener(schedulesList ->{
                            Toast.makeText(context, "STEP 1 PASSED", Toast.LENGTH_SHORT).show();
                            for (Schedule schedule : schedulesList) {
                                Log.e("StartTime", schedule.getStartTime().toString());
                                //Log.e("CurrentTime",((Long)currentTime.getSeconds()).toString());
                                Log.e("CurrentTime",currentTime.toString());

                                if(Math.abs(schedule.getStartTime().getSeconds()-currentTime.getSeconds())<1800){
                                    Toast.makeText(context, "STEP 2 PASSED", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                })
                        .addOnFailureListener(error ->{
                            Toast.makeText(context, "STEP 1 FAILED", Toast.LENGTH_SHORT).show();
                });
                //Toast.makeText(context, deviceID, Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, deviceIDNumberGetter.getIDNumber(), Toast.LENGTH_SHORT).show();



                /*smsNumber = "+14388313579"; //number of the monitoring user initiating the authentication
                smsMessage = "The authentication code is 123456";//response from supervised user with authentication code

                if(smsSender.equals(smsNumber)){
                    Toast.makeText(context, "Received BigOwl authentication SMS from: "+sender, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    try{
                        //sendSMS(context);
                    }
                    catch (Exception e){
                        // Todo: Add a warning system for failed authentication process
                    }
                }
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();*/
            }
        }
    }

}
