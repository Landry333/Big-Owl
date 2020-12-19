package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.bigowlapp.R;

public class SupervisedGroupPageActivity extends BigOwlActivity {
    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getStringExtra("groupID");
        initialize();
    }

    private void initialize() {
        Button btnListSchedule;
        btnListSchedule = findViewById(R.id.btn_schedule_list);
        btnListSchedule.setOnClickListener(v -> {
            Intent i = new Intent(com.example.bigowlapp.activity.SupervisedGroupPageActivity.this, ListOfScheduleActivity.class);
            i.putExtra("groupID", groupID);
            startActivity(i);
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_supervised_group_page;
    }
}