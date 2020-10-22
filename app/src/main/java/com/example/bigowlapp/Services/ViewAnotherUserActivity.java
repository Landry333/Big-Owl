package com.example.bigowlapp.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
//import com.google.firebase.auth.*;

public class ViewAnotherUserActivity extends AppCompatActivity {
    private Button supRequestBtn;
    String otherUserID = getIntent().getStringExtra("userID");
    String supRequestStatus = "none";
    String supBtn1="Send a request to supervise user";
    String supBtn2= "You are supervising this user";
    String supBtn3="Cancel this request to supervise";
    DatabaseReference mUserRef, supRequestRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_another_user);

        supRequestBtn = findViewById(R.id.SupRequest);
        supRequestRef = FirebaseDatabase.getInstance().getReference().child("SupRequests");
        mUser = mAuth.getCurrentUser();
        supRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoRequest(otherUserID);

            }
        });
    }

    private void DoRequest(String otherUserID) {
        if (supRequestStatus.equals("none")) {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "pending");
            supRequestRef.child(mUser.getUid()).child(otherUserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewAnotherUserActivity.this, "You have sent a request to supervise ", Toast.LENGTH_SHORT).show();
                        supRequestStatus = "sent_pending";
                        supRequestBtn.setText(supBtn3);
                    } else {
                        Toast.makeText(ViewAnotherUserActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (supRequestStatus.equals("sent_pending") || supRequestStatus.equals("sent_declined")) {
            supRequestRef.child(mUser.getUid()).child(otherUserID).removeValue().addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewAnotherUserActivity.this, "You have canceled request to supervise ", Toast.LENGTH_SHORT).show();
                        supRequestStatus = "none";
                        supRequestBtn.setText(supBtn1);
                    } else {
                        Toast.makeText(ViewAnotherUserActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (supRequestStatus.equals("accepted")) supRequestBtn.setText(supBtn2);
    }
}
