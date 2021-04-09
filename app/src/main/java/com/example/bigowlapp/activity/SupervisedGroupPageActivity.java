package com.example.bigowlapp.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.view_model.SupervisedGroupPageViewModel;

public class SupervisedGroupPageActivity extends BigOwlActivity {
    private String groupID;
    private String groupName;
    private String supervisorName;
    private String supervisorId;
    private Intent arrivingIntent;
    private Intent intentToScheduleList;
    private SupervisedGroupPageViewModel supervisedGroupPageViewModel;

    @Override
    protected void onStart() {
        super.onStart();
        if (arrivingIntent == null) {
            arrivingIntent = getIntent();
        }
        groupID = arrivingIntent.getStringExtra("groupID");
        groupName = arrivingIntent.getStringExtra("groupName");
        supervisorId = arrivingIntent.getStringExtra("supervisorId");

        if (supervisedGroupPageViewModel == null) {
            supervisedGroupPageViewModel = new ViewModelProvider(this).get(SupervisedGroupPageViewModel.class);
        }
        initialize();
    }

    private void initialize() {
        TextView groupNameView = findViewById(R.id.group_name_view);
        groupNameView.setText(groupName);

        supervisedGroupPageViewModel.getSupervisor(supervisorId).observe(this, supervisor -> {
            if (supervisor != null) {
                supervisorName = supervisor.getFullName();
                TextView supervisorNameView = findViewById(R.id.supervisor_name_view);
                supervisorNameView.setText(supervisorName);
            }
        });


        Button btnListSchedule = findViewById(R.id.btn_schedule_list);
        btnListSchedule.setOnClickListener(v -> {
            intentToScheduleList = new Intent(SupervisedGroupPageActivity.this, ListOfScheduleActivity.class);
            intentToScheduleList.putExtra("groupID", groupID);
            intentToScheduleList.putExtra("groupName", groupName);
            intentToScheduleList.putExtra("supervisorName", supervisorName);
            startActivity(intentToScheduleList);
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_supervised_group_page;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Intent getIntentToScheduleList() {
        return intentToScheduleList;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setSupervisedGroupListViewModel(SupervisedGroupPageViewModel supervisedGroupPageViewModel) {
        this.supervisedGroupPageViewModel = supervisedGroupPageViewModel;
    }
}