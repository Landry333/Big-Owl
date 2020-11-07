package com.example.bigowlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.viewModel.SupervisedGroupListViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

public class SupervisedGroupListActivity extends AppCompatActivity {
    private ListView supervisedGroups;
    private static SupervisedGroupListViewModel supervisedGroupListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_group_list);
        initialize();
    }

    protected void initialize() {
        try {
            supervisedGroupListViewModel = new ViewModelProvider(this).get(SupervisedGroupListViewModel.class);
            LiveData<List<Group>> listOfGroupsLiveData = supervisedGroupListViewModel.getSupervisedGroupList();

            listOfGroupsLiveData.observe(this, supervisedGroups -> {
                ArrayList<Group> groupsArrayList = new ArrayList<>(supervisedGroups);
                SupervisedGroupAdaptor adapter = new SupervisedGroupAdaptor(getBaseContext(), groupsArrayList);

                this.supervisedGroups = findViewById(R.id.supervised_groups);
                this.supervisedGroups.setAdapter(adapter);
                this.supervisedGroups.setOnItemClickListener((arg0, v, position, arg3) -> {
                    Intent intent = new Intent(getBaseContext(), SupervisedGroupPageActivity.class);
                    startActivity(intent);
                });
            });
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }


    private static class SupervisedGroupAdaptor extends ArrayAdapter<Group> {
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
            TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
            TextView groupSupervisor = (TextView) convertView.findViewById(R.id.groupSupervisor);

            groupName.setText(group.getName());

            // TODO: find a better way to do below without looping query in viewModel
            supervisedGroupListViewModel.getSupervisor(
                    group.getMonitoringUserId()).observe((LifecycleOwner) parent.getContext(),
                    supervisor -> {
                        groupSupervisor.setText(
                                supervisor.getFirstName()
                                        .concat(supervisor.getLastName()));
                    });
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

