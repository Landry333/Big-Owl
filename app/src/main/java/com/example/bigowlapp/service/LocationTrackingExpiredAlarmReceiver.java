package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.bigowlapp.utils.PeriodicLocationCheckAlarmManager;

/**
 * This will run when location tracking has expired for a user as a result of not getting
 * to a location at the expected time.
 */
public class LocationTrackingExpiredAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Reached LocationTrackingExpiredAlarmReceiver", Toast.LENGTH_LONG).show();
        PeriodicLocationCheckAlarmManager locationCheckAlarmManager = new PeriodicLocationCheckAlarmManager(context);
        locationCheckAlarmManager.cancelPeriodicLocationCheck();
    }
}
