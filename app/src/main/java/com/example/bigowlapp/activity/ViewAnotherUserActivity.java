package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
//import com.google.firebase.auth.*;

public class ViewAnotherUserActivity extends BigOwlActivity {
    /*String otherUserID = getIntent().getStringExtra("userID");*/
    String otherUserID;
    User otherUser;
    String supRequestStatus = "none";
    String supBtn1 = "Send a request to supervise user";
    String supBtn2 = "You are supervising this user";
    String supBtn3 = "Cancel this request to supervise";
    DatabaseReference supRequestRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private Button supRequestBtn;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String otherUserNumber = getIntent().getStringExtra("smsNumber");
    /*String contactNumber = getIntent().getStringExtra("contactNumber");*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supRequestBtn = findViewById(R.id.SupRequest);
        supRequestRef = FirebaseDatabase.getInstance().getReference().child("SupRequests");

        db.collection("users")
                .whereEqualTo("phoneNumber", otherUserNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult();
                            for (QueryDocumentSnapshot qds : task.getResult()) {
                                otherUser = qds.toObject(User.class);
                            }

                        }
                    }
                });

        otherUserID = otherUser.getUid();
        mUser = mAuth.getCurrentUser();
        supRequestBtn.setOnClickListener(v -> DoRequest(otherUserID));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_view_another_user;
    }

    private void DoRequest(String otherUserID) {
        if (supRequestStatus.equals("none")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", "pending");
            supRequestRef.child(mUser.getUid()).child(otherUserID)
                    .updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewAnotherUserActivity.this,
                            "You have sent a request to supervise ",
                            Toast.LENGTH_SHORT).show();
                    supRequestStatus = "sent_pending";
                    supRequestBtn.setText(supBtn3);
                } else {
                    Toast.makeText(ViewAnotherUserActivity.this,
                            "" + Objects.requireNonNull(task.getException()).toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (supRequestStatus.equals("sent_pending") || supRequestStatus.equals("sent_declined")) {
            supRequestRef.child(mUser.getUid()).child(otherUserID)
                    .removeValue()
                    .addOnCompleteListener((OnCompleteListener<Void>) task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewAnotherUserActivity.this,
                                    "You have canceled request to supervise ",
                                    Toast.LENGTH_SHORT).show();
                            supRequestStatus = "none";
                            supRequestBtn.setText(supBtn1);
                        } else {
                            Toast.makeText(ViewAnotherUserActivity.this,
                                    "" + Objects.requireNonNull(task.getException()).toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (supRequestStatus.equals("accepted")) supRequestBtn.setText(supBtn2);
    }
}
