package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;

public class SupervisedGroupPageActivity extends BigOwlActivity {
    private String groupID, groupName, supervisorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getStringExtra("groupID");
        groupName = getIntent().getStringExtra("groupName");
        supervisorName = getIntent().getStringExtra("supervisorName");
        initialize();
    }

    private void initialize() {
        TextView groupNameView  = findViewById(R.id.group_name_view);
        groupNameView.setText(groupName);

        Button btnListSchedule;
        btnListSchedule = findViewById(R.id.btn_schedule_list);
        btnListSchedule.setOnClickListener(v -> {
            Intent i = new Intent(com.example.bigowlapp.activity.SupervisedGroupPageActivity.this, ListOfScheduleActivity.class);
            i.putExtra("groupID", groupID);
            i.putExtra("groupName", groupName);
            i.putExtra("supervisorName", supervisorName);
            startActivity(i);
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_supervised_group_page;
    }
}