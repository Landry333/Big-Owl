package com.example.bigowlapp.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.example.bigowlapp.R;

import androidx.annotation.VisibleForTesting;

public class SupervisedGroupPageActivity extends BigOwlActivity {
    private String groupID, groupName, supervisorName;
    private Intent arrivingIntent;
    private Intent intentToScheduleList;

    @Override
    protected void onStart() {
        super.onStart();
        if (arrivingIntent == null) {
            arrivingIntent = getIntent();
        }
        groupID = arrivingIntent.getStringExtra("groupID");
        groupName = arrivingIntent.getStringExtra("groupName");
        supervisorName = arrivingIntent.getStringExtra("supervisorName");
        initialize();
    }

    private void initialize() {
        TextView groupNameView = findViewById(R.id.group_name_view);
        groupNameView.setText(groupName);

        Button btnListSchedule;
        btnListSchedule = findViewById(R.id.btn_schedule_list);
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
}