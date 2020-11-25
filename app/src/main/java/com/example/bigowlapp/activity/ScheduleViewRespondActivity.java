package com.example.bigowlapp.activity;

import android.os.Bundle;
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

public class ScheduleViewRespondActivity extends AppCompatActivity {
    String scheduleUId;
    private ScheduleResponseViewModel scheduleResponseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleUId = getIntent().getStringExtra("scheduleUId");
        setContentView(R.layout.activity_schedule_response);

        if (scheduleResponseViewModel == null) {
            scheduleResponseViewModel = new ViewModelProvider(this).get(ScheduleResponseViewModel.class);
        }

        scheduleResponseViewModel.getCurrentScheduleData(scheduleUId).observe(this, schedule -> {
            if (!scheduleResponseViewModel.isCurrentUserInSchedule()) {
                return;
            }
            TextView textViewSchedule = findViewById(R.id.text_view_schedule);
            // TODO front-end create multiple elements to handle multiple fields
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
        btnAccept.setOnClickListener(v -> userClickRespondButton(Response.ACCEPT));

        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(v -> userClickRespondButton(Response.REJECT));
    }

    private void userClickRespondButton(Response response) {
        if (scheduleResponseViewModel.isOneMinuteAfterLastResponse()) {
            scheduleResponseViewModel.respondSchedule(scheduleUId, response);

            // create type "memberResponseSchedule" notification
            scheduleResponseViewModel.notifySupervisorScheduleResponse();
        } else
            Toast.makeText(this,
                    "User can only respond to a schedule 1 minute after last response",
                    Toast.LENGTH_LONG).show();
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
}