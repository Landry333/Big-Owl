package com.example.bigowlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.view_model.SupervisedGroupListViewModel;

import java.util.ArrayList;
import java.util.List;

public class SupervisedGroupListActivity extends BigOwlActivity {
    private ListView supervisedGroupsListView;
    private SupervisedGroupListViewModel supervisedGroupListViewModel;
    private AlertDialog noGroupAlert;
    private Intent intentToSupervisedGroup;
    private boolean allowIntentForTest = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (supervisedGroupListViewModel == null) {
            supervisedGroupListViewModel = new ViewModelProvider(this).get(SupervisedGroupListViewModel.class);
        }
        subscribeToData();
    }

    private void subscribeToData() {
        try {
            setProgressBarVisible();
            supervisedGroupListViewModel.getCurrentUserData().observe(this,
                    currentUser -> supervisedGroupListViewModel.getSupervisedGroupListData().observe(this,
                            supervisedGroups -> {
                                if (supervisedGroups != null) {
                                    supervisedGroupsListView = findViewById(R.id.supervised_groups);
                                    supervisedGroupsListView.setAdapter(new SupervisedGroupAdaptor(getBaseContext(), new ArrayList<>(supervisedGroups)));
                                    supervisedGroupsListView.setOnItemClickListener((arg0, v, position, arg3) -> {
                                        intentToSupervisedGroup = new Intent(getBaseContext(), SupervisedGroupPageActivity.class);
                                        intentToSupervisedGroup.putExtra("groupID", supervisedGroups.get(position).getUid());
                                        intentToSupervisedGroup.putExtra("groupName", supervisedGroups.get(position).getName());
                                        intentToSupervisedGroup.putExtra("supervisorId", supervisedGroups.get(position).getSupervisorId());
                                        if (allowIntentForTest)
                                            startActivity(intentToSupervisedGroup);
                                    });
                                } else {
                                    this.noGroupAlert = new AlertDialog.Builder(SupervisedGroupListActivity.this)
                                            .setTitle("No supervised group found!")
                                            .setMessage("Required to be a supervised user of any group")
                                            .setPositiveButton("Ok", (dialogInterface, which) -> onBackPressed())
                                            .setCancelable(false)
                                            .create();
                                    this.noGroupAlert.show();
                                }
                                setProgressBarInvisible();
                            }));
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_supervised_group_list;
    }

    private class SupervisedGroupAdaptor extends ArrayAdapter<Group> {

        public SupervisedGroupAdaptor(@NonNull Context context, List<Group> groups) {
            super(context, 0, groups);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Group group = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.fragment_supervised_group_list_item, parent, false);
            }
            TextView groupName = convertView.findViewById(R.id.text_view_group_name);

            groupName.setText(group.getName());
            // Return the completed view to render on screen
            return convertView;
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setSupervisedGroupListViewModel(SupervisedGroupListViewModel supervisedGroupListViewModel) {
        this.supervisedGroupListViewModel = supervisedGroupListViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public AlertDialog getNoGroupAlert() {
        return this.noGroupAlert;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Intent getIntentToSupervisedGroupForTest() {
        return this.intentToSupervisedGroup;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setAllowIntentForTest(boolean allowIntentForTest) {
        this.allowIntentForTest = allowIntentForTest;
    }
}

