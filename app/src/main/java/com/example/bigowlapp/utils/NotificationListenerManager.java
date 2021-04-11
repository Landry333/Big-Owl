package com.example.bigowlapp.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationListenerManager {
    private static final String CHANNEL_ID = "General BigOwl Notification";

    private static ListenerRegistration listenerRegistration;

    public static void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    private SmsSender smsSender;
    private RepositoryFacade repositoryFacade;
    private Context context;
    private NotificationManager notificationManager;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;

    public NotificationListenerManager(Context context) {
        this.context = context;
        this.repositoryFacade = RepositoryFacade.getInstance();
        this.smsSender = new SmsSender();
        this.notificationManager = context.getSystemService(NotificationManager.class);
        this.notificationManagerCompat = NotificationManagerCompat.from(context);
        this.notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
    }

    public void listen() {
        if (listenerRegistration != null) {
            return;
        }

        listenerRegistration = repositoryFacade.getCurrentUserNotificationRepository()
                .listenToCollection((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(context, "BIG OWL notification listener failed: ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    resolveDocumentChanges(snapshots);
                });
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void resolveDocumentChanges(QuerySnapshot snapshots) {
        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            Notification notification = getNotificationFromDocument(dc);
            if (!notification.isValid() || !dc.getType().equals(DocumentChange.Type.ADDED)) {
                continue;
            }

            if (notification.getType() == Notification.Type.AUTH_BY_PHONE_NUMBER_FAILURE) {
                handleAuthByPhoneNumberFailure((AuthByPhoneNumberFailure) notification);
            } else {
                handleNotificationRequest(notification, context);
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public Notification getNotificationFromDocument(DocumentChange dc) {
        return NotificationRepository.getNotificationFromDocument(dc.getDocument(), Notification.class);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void handleAuthByPhoneNumberFailure(AuthByPhoneNumberFailure notification) {
        if (notification.isUsed() || notification.timeSinceCreationMillis() >= Schedule.MAX_TRACKING_TIME_MILLIS) {
            return;
        }

        String senderPhoneNum = notification.getSenderPhoneNum();
        String scheduleId = notification.getScheduleId();
        smsSender.sendSMS(senderPhoneNum, scheduleId);

        notification.setUsed(true);
        repositoryFacade.getCurrentUserNotificationRepository()
                .updateDocument(notification.getUid(), notification);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void handleNotificationRequest(Notification notification, Context context) {
        if (notification.isUsed()) {
            return;
        }

        createNotificationChannel();
        notificationBuilder(context, notification);

        notification.setUsed(true);
        repositoryFacade.getCurrentUserNotificationRepository()
                .updateDocument(notification.getUid(), notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        CharSequence name = "Notification";
        String description = "General notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    private void notificationBuilder(Context context, Notification notification) {
        PendingIntent contentIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, NotificationActivity.class)}, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.getTitle())
                .setStyle(new NotificationCompat.InboxStyle().addLine(notification.getMessage()))
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        this.sendNotification(notificationBuilder);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void sendNotification(NotificationCompat.Builder notificationBuilder) {
        notificationManagerCompat.notify(1, notificationBuilder.build());
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setRepositoryFacade(RepositoryFacade repositoryFacade) {
        this.repositoryFacade = repositoryFacade;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static ListenerRegistration getListenerRegistration() {
        return listenerRegistration;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static void setListenerRegistration(ListenerRegistration listenerRegistration) {
        NotificationListenerManager.listenerRegistration = listenerRegistration;
    }
}
