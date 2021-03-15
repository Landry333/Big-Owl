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
import com.example.bigowlapp.utils.PeriodicLocationCheckAlarmManager;

/**
 * This will run when location tracking has expired for a user as a result of not getting
 * to a location at the expected time.
 */
public class LocationTrackingExpiredAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "Missed Schedule";

    @Override
    public void onReceive(Context context, Intent intent) {
        PeriodicLocationCheckAlarmManager locationCheckAlarmManager = new PeriodicLocationCheckAlarmManager(context);
        locationCheckAlarmManager.cancelPeriodicLocationCheck();

        sendMissedScheduleNotification(context);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isInteractive()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(3000);
        }

        //String adsa = intent.getExtras("1");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("AlarmManager")
                .setContentText("test")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    public void sendMissedScheduleNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
