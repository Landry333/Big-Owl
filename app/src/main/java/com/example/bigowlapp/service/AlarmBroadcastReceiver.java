package com.example.bigowlapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Schedule;
import com.google.firebase.firestore.GeoPoint;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LATITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LONGITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this BroadcastReceiver is to execute code after the time activation of an alarm.
 * The alarms are set/defined in
 * {@link com.example.bigowlapp.utils.AlarmBroadcastReceiverManager}
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "testingChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Execute Schedule activatedSchedule = getSchedule(intent); to get the schedule
        // Run the location/Geofencing code
        // activatedSchedule should only have UID, LONGITUDE, LATITUDE
        // TODO: --START-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.
        createNotificationChannel(context);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isInteractive()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(3000);
        }
        Schedule schedule = getSchedule(intent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("AlarmManager")
                .setContentText(schedule.getUid() + "\n" +
                        schedule.getLocation().getLatitude() + "\n" +
                        schedule.getLocation().getLongitude())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
        // TODO: --END-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.
    }

    // TODO: --START-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    // TODO: --END-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.

    private Schedule getSchedule(Intent intent) {
        double defaultValueLatLng = 0.0;
        Schedule schedule = new Schedule();
        schedule.setUid(intent.getStringExtra(EXTRA_UID));
        GeoPoint geoPoint = new GeoPoint(intent.getDoubleExtra(EXTRA_LATITUDE, defaultValueLatLng),
                intent.getDoubleExtra(EXTRA_LONGITUDE, defaultValueLatLng));
        schedule.setLocation(geoPoint);
        return schedule;
    }

}
