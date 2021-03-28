package com.example.bigowlapp.utils;


import android.content.Context;
import android.widget.Toast;

import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.firestore.DocumentChange;

public class AuthFailureNotificationListener {

    private final RepositoryFacade repositoryFacade;

    public AuthFailureNotificationListener() {
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
                    }
                });
    }

    private void handleAuthByPhoneNumberFailure(AuthByPhoneNumberFailure notification) {
        if (notification.getType() == Notification.Type.AUTH_BY_PHONE_NUMBER_FAILURE
                && !notification.isUsed()
                && notification.timeSinceCreationMillis() < ScheduledLocationTrackingManager.DEFAULT_TRACKING_EXPIRE_TIME_MILLIS) {

            notification.setUsed(true);
            repositoryFacade.getCurrentUserNotificationRepository()
                    .updateDocument(notification.getUid(), notification);

            String senderPhoneNum = notification.getSenderPhoneNum();
            String scheduleId = notification.getScheduleId();
            SmsSender.sendSMS(senderPhoneNum, scheduleId);
        }
    }
}
