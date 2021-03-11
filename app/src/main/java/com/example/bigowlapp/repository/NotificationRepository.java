package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Field;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository extends Repository<Notification> {

    // TODO: Dependency Injection Implementation for Firestore
    public NotificationRepository() {
        super("notifications");
    }

    public MutableLiveData<List<SupervisionRequest>> getListOfSupervisionRequestByAttribute(String attribute, String attrValue,
                                                                                            Class<? extends SupervisionRequest> tClass) {
        MutableLiveData<List<SupervisionRequest>> listOfTData = new MutableLiveData<>();
        collectionReference.whereEqualTo(attribute, attrValue)
                .whereEqualTo(Field.Notification.TYPE, Notification.Type.SUPERVISION_REQUEST)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            List<SupervisionRequest> listOfT = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                SupervisionRequest t = doc.toObject(tClass);
                                listOfT.add(t);
                            }
                            listOfTData.setValue(listOfT);
                        } else {
                            listOfTData.setValue(null);
                        }
                    } else {
                        Log.e(tClass.getSimpleName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }

    @Override
    protected List<Notification> extractListOfDataToModel(QuerySnapshot results, Class<? extends Notification> tClass) {
        List<Notification> notificationsFromDb = new ArrayList<>();
        for (QueryDocumentSnapshot doc : results) {
            Notification.Type type = doc.toObject(Notification.class).getType();
            Notification t = doc.toObject(type.typeClass);
            notificationsFromDb.add(t);
        }
        return notificationsFromDb;
    }
}
