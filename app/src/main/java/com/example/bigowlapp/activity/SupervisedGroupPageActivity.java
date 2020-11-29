package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SupervisedGroupPageActivity extends AppCompatActivity {
    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_group_page);
        groupID = getIntent().getStringExtra("groupID");
        initialize();
    }

    private void initialize() {
        Button btnListSchedule;
        btnListSchedule = findViewById(R.id.btn_schedule_list);
        btnListSchedule.setOnClickListener(v -> {
            Intent i = new Intent(SupervisedGroupPageActivity.this, ListOfScheduleActivity.class);
            i.putExtra("groupID", groupID);
            startActivity(i);
        });
    }
}