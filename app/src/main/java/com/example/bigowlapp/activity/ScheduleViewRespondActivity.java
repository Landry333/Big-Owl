package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.viewModel.ScheduleViewRespondViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ScheduleViewRespondActivity extends AppCompatActivity {

    private String scheduleUId, groupName;
    private ScheduleViewRespondViewModel scheduleViewRespondViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleUId = getIntent().getStringExtra("scheduleUId");
        groupName = getIntent().getStringExtra("groupName");
        setContentView(R.layout.activity_schedule_response);

        if (scheduleViewRespondViewModel == null) {
            scheduleViewRespondViewModel = new ViewModelProvider(this).get(ScheduleViewRespondViewModel.class);
        }

        scheduleViewRespondViewModel.getCurrentScheduleData(scheduleUId).observe(this, schedule -> {
            if (!scheduleViewRespondViewModel.isCurrentUserInSchedule()) {
                return;
            }
            ((TextView) findViewById(R.id.text_view_group_uid)).setText(groupName);
            ((TextView) findViewById(R.id.text_view_group_supervisor_uid)).setText(schedule.getGroupSupervisorUId());
            ((TextView) findViewById(R.id.text_view_schedule_start_time)).setText(schedule.getStartTime().toDate().toString());
            ((TextView) findViewById(R.id.text_view_schedule_end_time)).setText(schedule.getEndTime().toDate().toString());

            UserScheduleResponse userScheduleResponse = scheduleViewRespondViewModel.getUserScheduleResponse();
            if (userScheduleResponse.getResponse() != Response.NEUTRAL) {
                ((TextView) findViewById(R.id.text_view_schedule_user_response_text))
                        .setText(userScheduleResponse.getResponse() == Response.ACCEPT ? "Accepted" : "Rejected");
                ((TextView) findViewById(R.id.text_view_schedule_user_response_time))
                        .setText(userScheduleResponse.getResponseTime().toDate().toString());
                (findViewById(R.id.linear_layout_response)).setVisibility(View.VISIBLE);
                findViewById(R.id.line_below_response).setVisibility(View.VISIBLE);
                setResponseButtonsVisibility(userScheduleResponse.getResponse());
            }
        });

        Button btnAccept = findViewById(R.id.button_accept);
        btnAccept.setOnClickListener(v -> userClickRespondButton(Response.ACCEPT));

        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(v -> userClickRespondButton(Response.REJECT));
    }

    private void userClickRespondButton(Response response) {
        if (scheduleViewRespondViewModel.isOneMinuteAfterLastResponse()) {
            scheduleViewRespondViewModel.respondSchedule(scheduleUId, response);
            scheduleViewRespondViewModel.notifySupervisorScheduleResponse();
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