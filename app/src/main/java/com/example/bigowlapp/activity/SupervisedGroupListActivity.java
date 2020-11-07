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
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

public class SupervisedGroupListActivity extends AppCompatActivity {
    private ListView lv;
    private SupervisedGroupListViewModel supervisedGroupListViewModel;
    private LiveData<List<Group>> listOfGroupsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_group_list);
        supervisedGroupListViewModel = new ViewModelProvider(this).get(SupervisedGroupListViewModel.class);
        initialize();
    }

    protected void initialize() {
        try {
            FirebaseUser currentUser = supervisedGroupListViewModel.getCurrentUser();
            if (currentUser != null) {
                listOfGroupsLiveData = supervisedGroupListViewModel.getSupervisedGroupList();

                listOfGroupsLiveData.observe(this, groups -> {
                    ArrayList<String> arrayGroupName = new ArrayList<>();
                    for (Group group : groups) {
                        arrayGroupName.add(group.getName());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, arrayGroupName);
                    lv = findViewById(R.id.listView_supervisedGroup);
                    lv.setAdapter(arrayAdapter);
                    // argument position gives the index of item which is clicked
                    lv.setOnItemClickListener((arg0, v, position, arg3) -> {
                        Intent intent = new Intent(getBaseContext(), SupervisedGroupPageActivity.class);
                        startActivity(intent);
                    });
                });
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private class GroupNameSupervisor {
        public String groupName;
        public String groupSupervisor;

        public GroupNameSupervisor(String groupName, String groupSupervisor) {
            this.groupName = groupName;
            this.groupSupervisor = groupSupervisor;
        }

        public GroupNameSupervisor() {
            this.groupName = "";
            this.groupSupervisor = "";
        }
    }

    private class GroupNameSupervisorAdapter extends ArrayAdapter<GroupNameSupervisor> {
        public GroupNameSupervisorAdapter(@NonNull Context context, ArrayList<GroupNameSupervisor> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            GroupNameSupervisor groupNameSupervisor = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_supervised_group_list, parent, false);
            }
            // Lookup view for data population
            TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
            TextView groupSupervisor = (TextView) convertView.findViewById(R.id.groupSupervisor);
            // Populate the data into the template view using the data object
            groupName.setText(groupNameSupervisor.groupName);
            groupSupervisor.setText(groupNameSupervisor.groupSupervisor);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

