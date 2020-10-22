package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Notification;

public class NotificationRepository extends Repository<Notification> {

    // TODO: Dependency Injection Implementation for Firestore
    public NotificationRepository() {
        super("notifications");
    }
}
