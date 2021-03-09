package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.example.bigowlapp.service.LocationTrackingExpiredAlarmReceiver;

/**
 * This will add alarms for when Location tracking has reached it's expire time, indicating
 * the user has not gotten to the expected location on time.
 */
public class LocationTrackingExpiredAlarmManager {

    private static final int REQUEST_CODE = 6;

    private final Context context;
    private final AlarmManager alarmManager;

    public LocationTrackingExpiredAlarmManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(long expireTimeMillis) {
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + expireTimeMillis,
                getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, LocationTrackingExpiredAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void cancelExpirationAlarm() {
        alarmManager.cancel(getPendingIntent());
    }
}
