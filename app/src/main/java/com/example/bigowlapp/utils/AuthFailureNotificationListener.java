package com.example.bigowlapp.utils;


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

public class AuthFailureNotificationListener {


    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    //private String smsNumber;
    //private String smsMessage;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final AuthRepository authRepository = new AuthRepository();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    private ScheduleRepository scheduleRepository = new ScheduleRepository();

    //scheduleRepository.get
    public AuthFailureNotificationListener() {

    }

    public void listen(Context context) {
        //final Notification[] document = new Notification[1];
        //final Document[] doc = new Document[1];
        db.collection("notifications")
                //.whereEqualTo("receiverUid", repositoryFacade.getAuthRepository().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(context, "Listen process failed: ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    //doc[0] = dc.getDocument().toObject(Document.class);
                                    //Log.e("ADDED", dc.getDocument().getData().toString());
                                    //document[0] = dc.getDocument().toObject(Notification.class);
                                    // Toast.makeText(context, "ADDED", Toast.LENGTH_SHORT).show();

                                            /*.getDocumentByUid(dc.getDocument().get("senderUid")
                                                    .toString(), User.class)
                                            .observe(this, user -> {
                                                String notificationSenderPhoneNum = user.getPhoneNumber();*/
                                    /*String notificationSenderPhoneNum = repositoryFacade.getUserRepository()
                                            .getDocumentByUid(dc.getDocument().get("senderUid")
                                                    .toString(), User.class)
                                            .getValue()
                                            .getPhoneNumber();*/

                                    //Log.e("notificationSenderPhoneNum", notificationSenderPhoneNum);


                                    //Log.e("DC", dc.getDocument().getData().toString());
                                    if (dc.getDocument().get("type").toString().equalsIgnoreCase("AUTH_BY_PHONE_NUMBER_FAILURE") &&
                                            dc.getDocument().get("receiverUid").toString().equalsIgnoreCase(repositoryFacade.getAuthRepository().getCurrentUser().getUid())) {

                                        /*repositoryFacade.getUserRepository().getDocumentByUid(dc.getDocument().get("receiverUid").toString(), User.class)
                                                .observe(this, user -> {

                                                });*/
                                        Log.e("DC", dc.getDocument().getData().toString());
                                        String notificationSenderPhoneNum= dc.getDocument().get("senderPhoneNum").toString();
                                        String scheduleId = dc.getDocument().get("scheduleId").toString();
                                        Log.e("notificationSenderPhoneNum", notificationSenderPhoneNum);
                                        sendSMS(context,notificationSenderPhoneNum,scheduleId);
                                        Toast.makeText(context, "Sending Auth2 SMS", Toast.LENGTH_SHORT).show();
                                    }
                                    break;


                                case MODIFIED:

                                    //Log.e("MODIFIED", dc.getDocument().getData().toString());
                                    //document[0] = dc.getDocument();
                                    //document[0] = (Notification) dc.getDocument().getData();
                                    //Log.e("MODIFIED 2 doc", dc.getDocument().get("type").toString());
                                    //Toast.makeText(context, dc.getDocument().get("type").toString(), Toast.LENGTH_SHORT).show();
                                    //break;
                                case REMOVED:
                                    //doc[0] = dc.getDocument().toObject(Document.class);
                                    //Log.e("REMOVED", dc.getDocument().getData().toString());
                                    // document[0] = dc.getDocument().toObject(Notification.class);
                                    //Toast.makeText(context, "REMOVED", Toast.LENGTH_SHORT).show();
                                    //break;
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

    private void sendSMS(Context context, String smsNumber, String smsMessage) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber, null, smsMessage, null, null);
        Toast.makeText(context, "SMS SENT", Toast.LENGTH_SHORT).show();
    }

}
