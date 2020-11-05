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
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MonitoringGroupPageActivity extends AppCompatActivity {
    private EditText searchUsers;
    private ListView usersListView;
    private TextView groupName;
    private List<User> mUsers, mUsersShow;

    private MonitoringGroupPageViewModel mGroupPageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_group_page);

        groupName = findViewById(R.id.group_name);
        usersListView = findViewById(R.id.users_list_view);
        searchUsers = findViewById(R.id.monitoring_group_search_users);

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

            mGroupPageViewModel.getUsersFromGroup(group).observe(this, users -> {
                mUsers = users;
                mUsersShow = mUsers;
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                usersListView.setAdapter((arrayAdapter));
                registerForContextMenu(usersListView);
            });
        });

        setupSearchBar();
    }

    private void setupSearchBar() {
        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
                ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow);
                usersListView.setAdapter(arrayAdapter);
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
        mUsersShow = mUsers.stream().filter(u -> {
            boolean containInFirstName = u.getFirstName().toLowerCase().contains(s);
            boolean containInLastName = u.getLastName().toLowerCase().contains(s);
            return (containInFirstName || containInLastName);
        }).collect(Collectors.toList());
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