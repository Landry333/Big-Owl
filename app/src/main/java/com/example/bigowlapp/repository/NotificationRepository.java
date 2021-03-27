package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository extends Repository<Notification> {

    // TODO: Dependency Injection Implementation for Firestore
    public NotificationRepository(String userUid) {
        super("users");
        collectionReference = collectionReference.document(userUid).collection("notifications");
    }

    @Override
    protected <X extends Notification> List<X> extractListOfDataToModel(QuerySnapshot results, Class<X> tClass) {
        List<X> notificationsFromDb = new ArrayList<>();
        for (QueryDocumentSnapshot doc : results) {
            X notification = getNotificationFromDocument(doc, tClass);
            if (notification.isValid()) {
                notificationsFromDb.add(notification);
            }
        }
        return notificationsFromDb;
    }

    public static <X extends Notification> X getNotificationFromDocument(DocumentSnapshot doc, Class<X> xClass) {
        Notification.Type type = doc.toObject(Notification.class).getType();
        if (type != null && (xClass == Notification.class || xClass == type.typeClass)) {
            return (X) doc.toObject(type.typeClass);
        }

        return (X) Notification.getInvalidNotification();
    }
}
