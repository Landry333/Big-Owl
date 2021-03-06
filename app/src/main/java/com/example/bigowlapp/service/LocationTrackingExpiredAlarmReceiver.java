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
import com.example.bigowlapp.utils.DateTimeFormatter;
import com.example.bigowlapp.utils.PeriodicLocationCheckAlarmManager;
import com.example.bigowlapp.utils.ScheduledLocationTrackingManager;

import java.util.Calendar;


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

        createNotificationChannel(context);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isInteractive()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(3000);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis()-ScheduledLocationTrackingManager.TRACKING_EXPIRE_TIME_MILLIS);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Missed Schedule for: " + intent.getStringExtra("schedule_title"))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("You've missed your schedule starting at:")
                        .addLine(DateTimeFormatter.dateAndTimeFormatter(calendar)));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Missed schedule";
            String description = "Notification for when the user missed a schedule he accepted by more than 30 minutes.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
