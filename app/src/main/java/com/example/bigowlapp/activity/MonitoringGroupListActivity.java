package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MonitoringGroupListActivity extends AppCompatActivity {
    EditText search_users;
    private ListView users_listview;
    private TextView groupName, supervisorName;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> mUsers, mUsersShow;
    private List<String> mSupervisedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_group_list);
        initialize();
    }

    protected void initialize() {
        mUsers = new ArrayList<>();
        mSupervisedGroup = new ArrayList<>();

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

                                        Group g = qds.toObject(Group.class);
                                        groupName = findViewById(R.id.textView_groupName);
                                        groupName.setText(qds.getString("name"));


                                        for (String supervisedUser : g.getSupervisedUserId()) {
                                            mSupervisedGroup.add(supervisedUser);
                                        }
                                        //find Supervisor full name
                                        db.collection("users").document(qds.getString("monitoringUserId")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    User u = task.getResult().toObject(User.class);
                                                    supervisorName = findViewById(R.id.textView_supervisorName);
                                                    supervisorName.setText(u.toString());
                                                }
                                            }
                                        });
                                    }

                                    //List of all user to compare with
                                    db.collection("users")
                                            .whereIn(FieldPath.documentId(), mSupervisedGroup)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        for (QueryDocumentSnapshot qds : task.getResult()) {
                                                            User u = qds.toObject(User.class);
                                                            mUsers.add(u);
                                                        }
                                                        mUsersShow = mUsers;
                                                        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<User>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                                                        users_listview = findViewById(R.id.list_view);
                                                        users_listview.setAdapter((arrayAdapter));
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d("test", "welp" + mUsers.size());
                searchUsers(charSequence.toString().toLowerCase());
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<User>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                users_listview.setAdapter((arrayAdapter));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void searchUsers(String s) {
/*
        if(s == null || s.equals(""))
        {
            mUsersShow = mUsers;
            return;
        }*/
        List<User> filteredUsers = mUsers.stream().filter(u -> {
            boolean containInFirstName = u.getFirstName().toLowerCase().contains(s);
            boolean containInLastName = u.getLastName().toLowerCase().contains(s);
            return (containInFirstName || containInLastName);
        }).collect(Collectors.toList());

        mUsersShow = filteredUsers;
    }
}