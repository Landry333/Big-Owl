package com.example.bigowlapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends AppCompatActivity {
    EditText search_users;
    private ListView users_listview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> mUsers, mUsersShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_users);
        initialize();
    }

    protected void initialize() {
        mUsers = new ArrayList<>();

        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {

                db.collection("group")

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

                Log.d("test", "welp" + mUsers.size());
                //searchUsers(charSequence.toString().toLowerCase());
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<User>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                users_listview.setAdapter((arrayAdapter));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
/*
    private void searchUsers(String s) {

        if(s == null || s.equals(""))
        {
            mUsersShow = mUsers;
            return;
        }
        List<User> filteredUsers = mUsers.stream().filter(u -> {
            boolean containInFirstName = u.getFirstName().toLowerCase().contains(s);
            boolean containInLastName = u.getLastName().toLowerCase().contains(s);
            return (containInFirstName || containInLastName);
        }).collect(Collectors.toList());

        mUsersShow = filteredUsers;
    }*/
}