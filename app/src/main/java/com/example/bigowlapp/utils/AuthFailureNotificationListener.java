package com.example.bigowlapp.utils;


import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

public class AuthFailureNotificationListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int THIRTY_MINUTES = 1800;
    private SmsSender smsSender = new SmsSender();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();


    public AuthFailureNotificationListener() {

    }

    public void listen(Context context) {
        db.collection("notifications")
                //.whereEqualTo("receiverUid", repositoryFacade.getAuthRepository().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(context, "BIG OWL notification listener failed: ", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType().equals(ADDED)) {
                                if (dc.getDocument().get("type").toString()
                                        .equalsIgnoreCase("AUTH_BY_PHONE_NUMBER_FAILURE")
                                        && dc.getDocument().get("receiverUid").toString()
                                        .equalsIgnoreCase(repositoryFacade.getAuthRepository().getCurrentUser().getUid())
                                        && !dc.getDocument().getBoolean("used")
                                        && ((Timestamp.now().getSeconds()
                                        - dc.getDocument().getTimestamp("creationTime").getSeconds()) < THIRTY_MINUTES)) {

                                    db.collection("notifications").document(dc.getDocument()
                                            .getId()).update("used", true);
                                    String notificationSenderPhoneNum = dc.getDocument().get("senderPhoneNum").toString();
                                    String scheduleId = dc.getDocument().get("scheduleId").toString();
                                    smsSender.sendSMS(context, notificationSenderPhoneNum, scheduleId);
                                }

                            }
                        }
                    }
                });

    }
}
