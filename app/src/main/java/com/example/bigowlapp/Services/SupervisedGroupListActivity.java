package com.example.bigowlapp.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bigowlapp.ActivityPage.SupervisedGroupPageActivity;
import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SupervisedGroupListActivity extends AppCompatActivity {
    private ListView lv;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<QueryDocumentSnapshot> qds = new ArrayList<QueryDocumentSnapshot>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_group_list);
        initialize();
    }

    protected void initialize() {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Query query = db.collection("groups").whereArrayContains("supervisedUserId", currentUser.getUid());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void  onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!qds.isEmpty())
                                        qds.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        qds.add(document);
                                    }
                                    ArrayAdapter<QueryDocumentSnapshot> arrayAdapter = new ArrayAdapter<QueryDocumentSnapshot>(getBaseContext(), android.R.layout.simple_list_item_1, qds);
                                    lv = findViewById(R.id.listView_supervisedGroup);
                                    lv.setAdapter(arrayAdapter);

                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        // argument position gives the index of item which is clicked
                                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
                                        {
                                            Intent intent = new Intent(getBaseContext(), SupervisedGroupPageActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

