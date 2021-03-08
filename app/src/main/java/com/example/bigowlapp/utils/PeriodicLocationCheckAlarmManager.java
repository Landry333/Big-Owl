package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

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

    public void setAlarm(int repeatIntervalMillis) {
        Intent intent = new Intent(context, PeriodicLocationCheckAlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                repeatIntervalMillis, pendingIntent);
    }
}
