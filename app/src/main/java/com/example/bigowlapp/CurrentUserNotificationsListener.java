package com.example.bigowlapp;


import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class CurrentUserNotificationsListener {


    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String smsNumber;
    private String smsMessage;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final AuthRepository authRepository = new AuthRepository();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    private ScheduleRepository scheduleRepository = new ScheduleRepository();

    //scheduleRepository.get
    public CurrentUserNotificationsListener() {

    }

    public void listen(Context context) {

        db.collection("notifications")
                .whereEqualTo("receiverUid", repositoryFacade.getAuthRepository().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            System.err.println("Listen failed: " + e);
                            Toast.makeText(context, "Listen failed: ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    System.out.println("New city: " + dc.getDocument().getData());
                                    Log.e("ADDED", dc.getDocument().getData().toString());
                                    Toast.makeText(context, "ADDED", Toast.LENGTH_SHORT).show();
                                    break;
                                case MODIFIED:
                                    System.out.println("Modified city: " + dc.getDocument().getData());
                                    Log.e("MODIFIED", dc.getDocument().getData().toString());
                                    Toast.makeText(context, "MODIFIED", Toast.LENGTH_SHORT).show();
                                    break;
                                case REMOVED:
                                    System.out.println("Removed city: " + dc.getDocument().getData());
                                    Log.e("REMOVED", dc.getDocument().getData().toString());
                                    Toast.makeText(context, "REMOVED", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });

        //public void listen() {
        //super.onStart();
        //LiveDataWithStatus<Notification> docRef = repositoryFacade.getNotificationRepository().getDocumentByUid("3MxRxAZXvUhmqTIPbTxb", Notification.class);
        /*DocumentReference docRef = db.collection("notifications").document();
                //.document("vwBFqWS6APUexdqOcP8t3UzmLrG2");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("ERROR", error.toString());
                    return;
                }

                if (value != null && value.exists()) {
                    Log.e("SNAPSHOT", value.toString());
                } else {
                    Log.e("SNAPSHOT NULL", "NULL SNAPSHOT");
                }
            }


        });*/
    }

    //DocumentReference docRef = db.collection("cities").document("SF");
    //docRef.addSnapshotListener(new EventListener<DocumentSnapshot>()

    private void sendSMS(Context context) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber, null, smsMessage, null, null);
        Toast.makeText(context, "SMS SENT", Toast.LENGTH_SHORT).show();
    }

}
