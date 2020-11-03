package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MonitoringGroupPageActivity extends AppCompatActivity {
    EditText search_users;
    private ListView users_listview;
    private TextView groupName;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> mUsers, mUsersShow;

    private MonitoringGroupPageViewModel mGroupPageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_group_list);

        groupName = findViewById(R.id.textView_groupName);
        users_listview = findViewById(R.id.list_view);
        search_users = findViewById(R.id.search_users);

        mGroupPageViewModel = new ViewModelProvider(this).get(MonitoringGroupPageViewModel.class);

        initialize();
    }

    protected void initialize() {
        mUsers = new ArrayList<>();

        mGroupPageViewModel.getGroup().observe(this, group -> {
            // TODO: better error or allow view page when accessing group with no users
            if (group == null || group.getSupervisedUserId() == null || group.getSupervisedUserId().isEmpty()) {
                this.noGroupAlert().show();
                return;
            }

            groupName.setText(group.getName());

            // TODO: deal with supervisor name
            //find Supervisor full name
//            db.collection("users").document(qds.getString("monitoringUserId")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        User u = task.getResult().toObject(User.class);
//                        supervisorName.setText(u.toString());
//                    }
//                }
//            });

            mGroupPageViewModel.getUsersFromGroup(group).observe(this, users -> {
                mUsers = users;
                mUsersShow = mUsers;
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                users_listview.setAdapter((arrayAdapter));
            });
        });

        setupSearchBar();
    }

    private void setupSearchBar() {
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                users_listview.setAdapter(arrayAdapter);
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

    private AlertDialog noGroupAlert() {
        return new AlertDialog.Builder(MonitoringGroupPageActivity.this)
                .setTitle("No monitoring group found")
                .setMessage("Required to be the Monitor of a group before accessing this list")
                .setPositiveButton("Ok", (dialogInterface, which) -> MonitoringGroupPageActivity.super.onBackPressed())
                .setCancelable(false)
                .create();
    }
}