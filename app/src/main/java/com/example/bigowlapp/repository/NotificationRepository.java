package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class NotificationRepository extends Repository<Notification> {

    @Inject
    public NotificationRepository(FirebaseFirestore mFirebaseFirestore) {
        super("notifications", mFirebaseFirestore);
    }
}
