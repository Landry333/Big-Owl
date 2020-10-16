package com.example.bigowlapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MonitoringGroupPageActivity extends AppCompatActivity {
    private TextView groupName, supervisorName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String supervisorFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_group_page);
        initialize();
    }

    protected void initialize() {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                db.collection("groups")
                        .whereEqualTo("monitoringUserId", currentUser.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot qds : task.getResult()) {
                                        groupName = findViewById(R.id.textView_groupName);
                                        groupName.setText(qds.getString("name"));

                                        supervisorName = findViewById(R.id.textView_supervisorName);
                                        setSupervisorFullName(qds);
                                        supervisorName.setText(supervisorFullName);
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSupervisorFullName(final QueryDocumentSnapshot qds) {
        DocumentReference docRef = db.collection("users").document(qds.getString("monitoringUserId"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dSS = task.getResult();
                    supervisorFullName = (dSS.getString("firstName") + dSS.getString("lastName"));
                }
            }
        });
    }
}