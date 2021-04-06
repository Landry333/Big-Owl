package com.example.bigowlapp.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.NotificationActivity;
import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.firestore.DocumentChange;

public class NotificationListenerManager {

    private final RepositoryFacade repositoryFacade;

    public NotificationListenerManager() {
        this.repositoryFacade = RepositoryFacade.getInstance();
    }

    public void listen(Context context) {
        repositoryFacade.getCurrentUserNotificationRepository()
                .listenToCollection((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(context, "BIG OWL notification listener failed: ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    assert snapshots != null;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Notification notification = NotificationRepository.getNotificationFromDocument(dc.getDocument(), Notification.class);
                        if (!notification.isValid() || !dc.getType().equals(DocumentChange.Type.ADDED)) {
                            continue;
                        }

                        if (notification.getType() == Notification.Type.AUTH_BY_PHONE_NUMBER_FAILURE) {
                            handleAuthByPhoneNumberFailure((AuthByPhoneNumberFailure) notification);
                        }
                        else{
                            handleNotificationRequest(notification, context);
                        }
                    }
                });
    }

    private void handleAuthByPhoneNumberFailure(AuthByPhoneNumberFailure notification) {
        if (!notification.isUsed()
                && notification.timeSinceCreationMillis() < Schedule.MAX_TRACKING_TIME_MILLIS) {

            String senderPhoneNum = notification.getSenderPhoneNum();
            String scheduleId = notification.getScheduleId();
            SmsSender.sendSMS(senderPhoneNum, scheduleId);

            notification.setUsed(true);
            repositoryFacade.getCurrentUserNotificationRepository()
                    .updateDocument(notification.getUid(), notification);
        }
    }

    private void handleNotificationRequest(Notification notification, Context context){
        if (notification.isUsed()) {
            return;
        }
        String channel = notification.getTitle();
        createNotificationChannel(context, channel);

        notification.setUsed(true);
        repositoryFacade.getCurrentUserNotificationRepository()
                .updateDocument(notification.getUid(), notification);

        notificationBuilder(context, channel, notification);

        notification.setUsed(true);
        repositoryFacade.getCurrentUserNotificationRepository()
                .updateDocument(notification.getUid(), notification);
    }

    private void createNotificationChannel(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "General notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notificationBuilder(Context context, String channel, Notification notification){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(channel)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(notification.getMessage()));
        PendingIntent contentIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, NotificationActivity.class)}, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
