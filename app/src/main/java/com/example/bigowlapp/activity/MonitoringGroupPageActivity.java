package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.MonitoringGroupPageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class MonitoringGroupPageActivity extends BigOwlActivity {
    private EditText searchUsers;
    private ListView usersListView;
    private TextView groupName;
    private List<User> mUsers;
    private List<User> mUsersShow;
    private User contextMenuSelectedUser;
    private Button btnSetSchedule;
    private Button btnViewSchedule;
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
        btnSetSchedule = findViewById(R.id.btn_set_schedule);

        btnSetSchedule.setOnClickListener(v -> {
            Intent i = new Intent(MonitoringGroupPageActivity.this, SetScheduleActivity.class);
            startActivity(i);
        });
    }

    private void subscribeToData() {
        if (!mGroupPageViewModel.isCurrentUserSet()) {
            return;
        }

        setProgressBarVisible();
        mGroupPageViewModel.getGroup().observe(this, group -> {
            if (group == null) {
                createAlertDialog("No monitoring group found",
                        "Required to be the Monitor of a group before accessing this list");
            } else if (group.getMemberIdList() == null || group.getMemberIdList().isEmpty()) {
                createAlertDialog("No supervised member(s) found",
                        "Required to have supervised member(s) before accessing this list");
            } else {
                groupName.setText(group.getName());
                mGroupPageViewModel.getUsersFromGroup(group).observe(this, users -> {
                    mUsers = users;
                    mUsersShow = mUsers;
                    resetUsersListViewAdapter();
                    registerForContextMenu(usersListView);
                });
            }

            btnViewSchedule = findViewById(R.id.btn_view_schedule);
            btnViewSchedule.setOnClickListener(v -> {
                Intent intentToScheduleList = new Intent(this, ListOfScheduleActivity.class);
                intentToScheduleList.putExtra("groupID", group.getUid());
                intentToScheduleList.putExtra("groupName", group.getName());
                intentToScheduleList.putExtra("supervisorId", group.getSupervisorId());
                intentToScheduleList.putExtra("isUserTheGroupSupervisor", true);
                startActivity(intentToScheduleList);
            });
            setProgressBarInvisible();
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
                // Nothing needs to be done before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase(Locale.getDefault()));
                resetUsersListViewAdapter();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing needs to be done after the text is changed
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
        String menuItemName = getResources().getStringArray(R.array.context_menu)[item.getItemId()];

        if (menuItemName.equals("Remove")) {
            mGroupPageViewModel.removeUserFromGroup(contextMenuSelectedUser);
            mUsers.remove(contextMenuSelectedUser);
            resetUsersListViewAdapter();
        }

        return true;
    }

    protected void searchUsers(String s) {
        mUsersShow = mUsers.stream().filter(u -> {
            boolean containInFirstName = u.getFirstName().toLowerCase(Locale.getDefault()).contains(s);
            boolean containInLastName = u.getLastName().toLowerCase(Locale.getDefault()).contains(s);
            boolean containInFullName = u.getFullName().toLowerCase(Locale.getDefault()).contains(s);
            return (containInFirstName || containInLastName || containInFullName);
        }).collect(Collectors.toList());
    }

    protected void createAlertDialog(String title, String message) {
        new AlertDialog.Builder(MonitoringGroupPageActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialogInterface, which) -> MonitoringGroupPageActivity.super.onBackPressed())
                .setCancelable(false)
                .create()
                .show();
    }

    protected void resetUsersListViewAdapter() {
        usersListView.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mUsersShow));
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setmGroupPageViewModel(MonitoringGroupPageViewModel mGroupPageViewModel) {
        this.mGroupPageViewModel = mGroupPageViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public MonitoringGroupPageViewModel getmGroupPageViewModel() {
        return this.mGroupPageViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public List<User> getmUsersShow() {
        return this.mUsersShow;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public ListView getUsersListView() {
        return usersListView;
    }
}