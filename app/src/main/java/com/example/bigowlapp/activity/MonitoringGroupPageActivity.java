package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MonitoringGroupPageActivity extends AppCompatActivity {
    EditText search_users;
    private ListView users_list_view;
    private TextView groupName, supervisorName;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> mUsers, mUsersShow;
    private List<String> mSupervisedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_group_list);
        initialize();
        registerForContextMenu(users_list_view);
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
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot qds : Objects.requireNonNull(task.getResult())) {

                                    Group g = qds.toObject(Group.class);
                                    groupName = findViewById(R.id.textView_groupName);
                                    groupName.setText(qds.getString("name"));

                                    mSupervisedGroup.addAll(g.getSupervisedUserId());

                                    //find Supervisor full name
                                    db.collection("users")
                                            .document(Objects.requireNonNull(qds.getString("monitoringUserId")))
                                            .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            User u = Objects.requireNonNull(task1.getResult()).toObject(User.class);
                                            supervisorName = findViewById(R.id.textView_supervisorName);
                                            supervisorName.setText(Objects.requireNonNull(u).toString());
                                        }
                                    });
                                }

                                if (!mSupervisedGroup.isEmpty()) {
                                    //List of all user to compare with
                                    db.collection("users")
                                            .whereIn(FieldPath.documentId(), mSupervisedGroup)
                                            .get()
                                            .addOnCompleteListener(task12 -> {
                                                if (task12.isSuccessful()) {

                                                    for (QueryDocumentSnapshot qds : Objects.requireNonNull(task12.getResult())) {
                                                        User u = qds.toObject(User.class);
                                                        mUsers.add(u);
                                                    }
                                                    mUsersShow = mUsers;
                                                    users_list_view = findViewById(R.id.user_list_view);
                                                    ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                                                    users_list_view.setAdapter((arrayAdapter));
                                                }
                                            });
                                } else {
                                    new AlertDialog.Builder(MonitoringGroupPageActivity.this)
                                            .setTitle("No monitoring group found")
                                            .setMessage("Required to be the Monitor of a group before accessing this list")
                                            .setPositiveButton("Ok", (dialogInterface, which) -> MonitoringGroupPageActivity.super.onBackPressed())
                                            .setCancelable(false)
                                            .create()
                                            .show();
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
                searchUsers(charSequence.toString().toLowerCase());
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                users_list_view.setAdapter((arrayAdapter));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) menuInfo;
                String country = ((TextView) info.targetView).getText().toString();
                menu.setHeaderTitle(country);

                String[] actions = getResources().getStringArray(R.array.context_menu);
                for (int i = 0; i < actions.length; i++) {
                    menu.add(Menu.NONE, i, i, actions[i]);
                }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        String [] menuItems = getResources().getStringArray(R.array.context_menu);
        String menuItemName = menuItems[menuItemIndex];

        switch (menuItemName) {

            case "Remove":
                break;

        }

        return true;
    }

    private void searchUsers(String s) {
/*
        if(s == null || s.equals(""))
        {
            mUsersShow = mUsers;
            return;
        }*/

        mUsersShow = mUsers.stream().filter(u -> {
            boolean containInFirstName = u.getFirstName().toLowerCase().contains(s);
            boolean containInLastName = u.getLastName().toLowerCase().contains(s);
            return (containInFirstName || containInLastName);
        }).collect(Collectors.toList());
    }
}