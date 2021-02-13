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

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.MonitoringGroupPageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;


public class MonitoringGroupPageActivity extends BigOwlActivity {
    private EditText searchUsers;
    private ListView usersListView;
    private TextView groupName;
    private List<User> mUsers, mUsersShow;
    private User contextMenuSelectedUser;

    private MonitoringGroupPageViewModel mGroupPageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupName = findViewById(R.id.text_view_group_name);
        usersListView = findViewById(R.id.list_view_monitoring_users);
        searchUsers = findViewById(R.id.monitoring_group_search_users);

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGroupPageViewModel == null) {
            mGroupPageViewModel = new ViewModelProvider(this).get(MonitoringGroupPageViewModel.class);
        }
        subscribeToData();
    }

    protected void initialize() {
        mUsers = new ArrayList<>();
        setupSearchBar();
    }

    private void subscribeToData() {
        if (!mGroupPageViewModel.isCurrentUserSet()) {
            return;
        }

        mGroupPageViewModel.getGroup().observe(this, group -> {
            // TODO: better error or allow view page when accessing group with no users
            if (group == null || group.getMemberIdList() == null || group.getMemberIdList().isEmpty()) {
                this.noGroupAlert().show();
                return;
            }

            groupName.setText(group.getName());

            mGroupPageViewModel.getUsersFromGroup(group).observe(this, users -> {
                mUsers = users;
                mUsersShow = mUsers;
                resetUsersListViewAdapter();
                registerForContextMenu(usersListView);
            });
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_monitoring_group_page;
    }

    private void setupSearchBar() {
        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
                resetUsersListViewAdapter();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextMenuSelectedUser = mUsersShow.get((int) info.id);

        String[] actions = getResources().getStringArray(R.array.context_menu);
        for (int i = 0; i < actions.length; i++) {
            menu.add(Menu.NONE, i, i, actions[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.context_menu);
        String menuItemName = menuItems[menuItemIndex];

        switch (menuItemName) {
            case "View Profile":
                break;
            case "Remove": {
                mGroupPageViewModel.removeUserFromGroup(contextMenuSelectedUser);
                mUsers.remove(contextMenuSelectedUser);
                resetUsersListViewAdapter();
            }
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

    private void resetUsersListViewAdapter() {
        usersListView.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow));
    }

    @VisibleForTesting
    public void setmGroupPageViewModel(MonitoringGroupPageViewModel mGroupPageViewModel) {
        this.mGroupPageViewModel = mGroupPageViewModel;
    }

    @VisibleForTesting
    public MonitoringGroupPageViewModel getmGroupPageViewModel() {
        return this.mGroupPageViewModel;
    }
}