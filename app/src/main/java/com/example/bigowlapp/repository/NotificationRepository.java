package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.NullNotification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository extends Repository<Notification> {
    public static final String COLLECTION_NAME = "notifications";

    // TODO: Dependency Injection Implementation for Firestore
    public NotificationRepository(String userUid) {
        super(UserRepository.COLLECTION_NAME);
        collectionReference = collectionReference.document(userUid)
                .collection(NotificationRepository.COLLECTION_NAME);
    }

    @Override
    protected <X extends Notification> List<X> extractListOfDataToModel(QuerySnapshot results, Class<X> tClass) {
        List<X> notificationsFromDb = new ArrayList<>();
        for (QueryDocumentSnapshot doc : results) {
            Notification notification = getNotificationFromDocument(doc, tClass);
            if (notification.isValid()) {
                notificationsFromDb.add((X) notification);
            }
        }
        return notificationsFromDb;
    }


    public <X extends Notification> LiveDataWithStatus<List<X>> getNotificationsByAscendingOrder(Class<X> tClass) {
        LiveDataWithStatus<List<X>> listOfTData = new LiveDataWithStatus<>();
        collectionReference
                .orderBy(Notification.Field.CREATION_TIME, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setSuccess(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setError(getDocumentNotFoundException(tClass));
                        }
                    } else {
                        listOfTData.setError(task.getException());
                    }
                });
        return listOfTData;
    }


    public static <X extends Notification> Notification getNotificationFromDocument(DocumentSnapshot doc, Class<X> xClass) {
        Notification notification = Notification.getNotificationSafe(doc.toObject(Notification.class));
        Notification.Type type = notification.getType();

        if (xClass == Notification.class || xClass == type.typeClass) {
            return doc.toObject(type.typeClass);
        }

        return new NullNotification();
    }
}
