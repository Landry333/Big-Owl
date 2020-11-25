package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.viewModel.ScheduleResponseViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ScheduleResponseActivity extends AppCompatActivity {
    String scheduleUId;
    private ScheduleResponseViewModel scheduleResponseViewModel;
    private boolean doubleBackToExitPressedOnce = false;

    // groupUId ajtkmncA3SOauyMgDV0x (ng_...)
    // supervisorUId CBYGDbEW1MfMYcTCbiiccYxwIyZ2 (ng_...)
    // memberUid 1Yilto5zfkOZYaRCWprRscEs2hE3 (circle...)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleUId = getIntent().getStringExtra("scheduleUId");
        setContentView(R.layout.activity_schedule_response);

        // if (scheduleResponseViewModel.isCurrentUserInSchedule())

        if (scheduleResponseViewModel == null) {
            scheduleResponseViewModel = new ViewModelProvider(this).get(ScheduleResponseViewModel.class);
        }

        scheduleResponseViewModel.getCurrentScheduleData(scheduleUId).observe(this, schedule -> {
            TextView textViewSchedule = findViewById(R.id.text_view_schedule);
            String scheduleText = "scheduleUid: \n" + scheduleUId + "\n\n" +
                    "groupUId: \n" + schedule.getGroupUId() + "\n\n" +
                    "groupSupervisorUId: \n" + schedule.getGroupSupervisorUId() + "\n\n" +
                    "location: \n" + schedule.getLocation() + "\n\n" +
                    "startTime: \n" + schedule.getStartTime().toDate() + "\n\n" +
                    "getEndTime: \n" + schedule.getEndTime().toDate() + "\n\n";
            textViewSchedule.setText(scheduleText);

            TextView textViewResponse = findViewById(R.id.text_view_response);
            View viewLineBelowResponse = findViewById(R.id.line_below_response);
            Schedule.UserResponse userResponse = scheduleResponseViewModel.getUserResponseInSchedule();
            if (userResponse.getResponse() != Response.NEUTRAL) {
                String responseText = "You last " + (userResponse.getResponse() == Response.ACCEPT ? "accepted" : "rejected") +
                        " this schedule on \n"
                        + userResponse.getResponseTime().toDate().toString();
                textViewResponse.setText(responseText);
                textViewResponse.setVisibility(View.VISIBLE);
                viewLineBelowResponse.setVisibility(View.VISIBLE);
                setResponseButtonsVisibility(userResponse.getResponse());
            }
        });

        Button btnAccept = findViewById(R.id.button_accept);
        btnAccept.setOnClickListener(v -> {
            scheduleResponseViewModel.responseSchedule(scheduleUId, Response.ACCEPT);

            // create type "memberResponseSchedule" notification
            scheduleResponseViewModel.notifySupervisorScheduleResponse();
        });

        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(v -> {
            scheduleResponseViewModel.responseSchedule(scheduleUId, Response.REJECT);

            // create type "memberResponseSchedule" notification
        });
    }

    private void setResponseButtonsVisibility(Response response) {
        Button btnAccept = findViewById(R.id.button_accept);
        Button btnReject = findViewById(R.id.button_reject);
        if (response == Response.NEUTRAL) {
            btnAccept.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
        } else if (response == Response.ACCEPT) {
            btnAccept.setVisibility(View.GONE);
            btnReject.setVisibility(View.VISIBLE);
        } else if (response == Response.REJECT) {
            btnAccept.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.GONE);
        } else {
            btnAccept.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            startActivity(new Intent(this, HomePageActivity.class));
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}