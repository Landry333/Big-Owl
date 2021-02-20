package com.example.bigowlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.viewModel.SupervisedGroupListViewModel;

import java.util.ArrayList;
import java.util.List;

public class SupervisedGroupListActivity extends BigOwlActivity {
    private ListView supervisedGroupsListView;
    private SupervisedGroupListViewModel supervisedGroupListViewModel;
    private String supervisorName;
    private List<String> supervisorNameList = new ArrayList<String>();

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
            supervisedGroupListViewModel.getCurrentUserData().observe(this,
                    currentUser -> supervisedGroupListViewModel.getSupervisedGroupListData().observe(this,
                            supervisedGroups -> {
                                if (supervisedGroups != null) {
                                    supervisedGroupsListView = findViewById(R.id.supervised_groups);
                                    supervisedGroupsListView.setAdapter(new SupervisedGroupAdaptor(getBaseContext(), new ArrayList<>(supervisedGroups)));
                                    supervisedGroupsListView.setOnItemClickListener((arg0, v, position, arg3) -> {
                                        Intent intent = new Intent(getBaseContext(), SupervisedGroupPageActivity.class);
                                        intent.putExtra("groupID", supervisedGroups.get(position).getUid());
                                        intent.putExtra("groupName", supervisedGroups.get(position).getName());
                                        intent.putExtra("supervisorName", supervisorNameList.get(position));
                                        startActivity(intent);
                                    });
                                } else
                                    this.noGroupAlert().show();
                            }));
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_supervised_group_list;
    }

    private class SupervisedGroupAdaptor extends ArrayAdapter<Group> {

        public SupervisedGroupAdaptor(@NonNull Context context, ArrayList<Group> groups) {
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
            TextView groupSupervisor = convertView.findViewById(R.id.text_view_group_supervisor);

            groupName.setText(group.getName());

            // TODO: find a better way to do below without looping query in viewModel
            supervisedGroupListViewModel.getSupervisor(
                    group.getSupervisorId()).observe((LifecycleOwner) parent.getContext(),
                    supervisor -> {
                        groupSupervisor.setText(supervisor.getFullName());
                        supervisorNameList.add(supervisor.getFullName());
                    });
            // Return the completed view to render on screen
            return convertView;
        }
    }

    private AlertDialog noGroupAlert() {
        return new AlertDialog.Builder(SupervisedGroupListActivity.this)
                .setTitle("No supervised group found!")
                .setMessage("Required to be a supervised user of any group")
                .setPositiveButton("Ok", (dialogInterface, which) -> SupervisedGroupListActivity.super.onBackPressed())
                .setCancelable(false)
                .create();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setSupervisedGroupListViewModel(SupervisedGroupListViewModel supervisedGroupListViewModel) {
        this.supervisedGroupListViewModel = supervisedGroupListViewModel;
    }
}

