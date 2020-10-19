package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.utils.Constants;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository extends Repository<Notification> {

    // TODO: Dependency Injection Implementation for Firestore
    public NotificationRepository() {
        super("notifications");
    }

    @Override
    public MutableLiveData<List<Notification>> getAllDocumentsFromCollection(Class<? extends Notification> tClass) {
        MutableLiveData<List<Notification>> listOfTData = new MutableLiveData<>();
        this.collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setValue(this.createNotificationListUsingType(task.getResult()));
                        } else {
                            listOfTData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }

    private List<Notification> createNotificationListUsingType(QuerySnapshot results) {
        List<Notification> notificationsFromDb = new ArrayList<>();
        for (QueryDocumentSnapshot doc : results) {
            String type = doc.toObject(Notification.class).getType();
            Notification t = doc.toObject(this.getClassFromType(type));
            notificationsFromDb.add(t);
        }
        return notificationsFromDb;
    }

    private Class<? extends Notification> getClassFromType(String type) {
        if (type.equals(Constants.SUPERVISION_TYPE)) {
            return SupervisionRequest.class;
        } else {
            return Notification.class;
        }
    }
}
