package com.example.bigowlapp.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.example.bigowlapp.R;

import java.util.Random;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

// TODO remove the whole activity
public class ScheduleTestActivity extends AppCompatActivity {
    final String channelId = "ScheduleNotificationChannelId";
    NotificationCompat.Builder builder;
    int nid;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_test);

        createNotificationChannel();

        Intent intent = new Intent(this, ScheduleResponseActivity.class);
        intent.putExtra("scheduleUId", "KZ-TEST");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Random notification_id = new Random();
        nid = notification_id.nextInt();

        builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_square)
                .setContentTitle("Schedule Test")
                .setContentText("You have a schedule" + nid)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Button btnNotify = findViewById(R.id.button_notify);
        btnNotify.setOnClickListener(v -> {
            notificationManager.notify(nid, builder.build());
            nid = notification_id.nextInt();
            builder.setContentText("You have a schedule - " + nid);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        //CharSequence name = getString(R.string.channel_name);
        CharSequence name = "channelName";
        //String description = getString(R.string.channel_description);
        String description = "channelDescription";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}