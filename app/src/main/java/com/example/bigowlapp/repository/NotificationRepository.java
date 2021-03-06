package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Notification;
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
            Notification.Type type = doc.toObject(Notification.class).getType();
            if (tClass == Notification.class || tClass == type.typeClass) {
                X t = (X) doc.toObject(type.typeClass);
                notificationsFromDb.add(t);
            }
        }
        return notificationsFromDb;
    }
}
