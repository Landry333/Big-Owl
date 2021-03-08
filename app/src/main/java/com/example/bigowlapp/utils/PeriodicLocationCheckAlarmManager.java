package com.example.bigowlapp.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

// TODO: Might want to create a super AlarmManger given there are multiple now

/**
 * Create alarm that will force a location check periodically
 */
public class PeriodicLocationCheckAlarmManager {

    private static final int REQUEST_CODE = 0;
    private final Context context;
    private final AlarmManager alarmManager;

    public PeriodicLocationCheckAlarmManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    // TODO: need to find way to cancel the alarm when complete

    public void setAlarm(int repeatIntervalMillis) {
        PendingIntent pendingIntent = getPendingIntent();

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                repeatIntervalMillis, pendingIntent);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, PeriodicLocationCheckAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void cancelPeriodicLocationCheck() {
        PendingIntent pendingIntent = getPendingIntent();
        alarmManager.cancel(pendingIntent);
    }

    public static class PeriodicLocationCheckAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(context);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null);
        }
    }
}
