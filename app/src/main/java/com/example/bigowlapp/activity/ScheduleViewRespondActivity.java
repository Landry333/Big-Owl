package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.utils.PermissionsHelper;
import com.example.bigowlapp.viewModel.ScheduleViewRespondViewModel;

import java.util.Collections;

public class ScheduleViewRespondActivity extends BigOwlActivity {

    private String scheduleUid, groupName, supervisorName;
    private ScheduleViewRespondViewModel scheduleViewRespondViewModel;
    private PermissionsHelper permissionsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        scheduleUid = intent.getStringExtra("scheduleUid");
        groupName = intent.getStringExtra("groupName");
        supervisorName = intent.getStringExtra("supervisorName");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (scheduleViewRespondViewModel == null) {
            scheduleViewRespondViewModel = new ViewModelProvider(this).get(ScheduleViewRespondViewModel.class);
        }

        subscribeToData();
    }

    private void subscribeToData() {
        if (!scheduleViewRespondViewModel.isCurrentUserSet()) {
            return;
        }

        scheduleViewRespondViewModel.getCurrentScheduleData(scheduleUid).observe(this, schedule -> {
            if (!scheduleViewRespondViewModel.isCurrentUserInSchedule()) {
                return;
            }

            ((TextView) findViewById(R.id.text_view_group_uid)).setText(groupName);
            ((TextView) findViewById(R.id.text_view_group_supervisor_name)).setText(supervisorName);
            ((TextView) findViewById(R.id.text_view_schedule_start_time)).setText(schedule.getStartTime().toDate().toString());
            ((TextView) findViewById(R.id.text_view_schedule_end_time)).setText(schedule.getEndTime().toDate().toString());

            UserScheduleResponse userScheduleResponse = scheduleViewRespondViewModel.getUserScheduleResponse();
            if (userScheduleResponse != null && userScheduleResponse.getResponse() != Response.NEUTRAL) {
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
        btnAccept.setOnClickListener(v -> {
            requestLocationPermissions();
            userClickRespondButton(Response.ACCEPT);
        });

        Button btnReject = findViewById(R.id.button_reject);
        btnReject.setOnClickListener(v -> userClickRespondButton(Response.REJECT));
    }

    public void requestLocationPermissions() {
        this.permissionsHelper = new PermissionsHelper(this);

        String reason = "Location detection is required to check whether you have arrived to a scheduled location." +
                "\n\nIf not provided, the system will assume you have not arrived to the location.";
        permissionsHelper.requestMissingPermissions(
                Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION),
                reason,
                PermissionsHelper.REQUEST_ALSO_REQUEST_BACKGROUND_LOCATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                !permissionsHelper.isMissingPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsHelper.requestBackgroundLocationPermissions();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_schedule_response;
    }

    private void userClickRespondButton(Response response) {
        if (scheduleViewRespondViewModel.isOneMinuteAfterLastResponse()) {
            scheduleViewRespondViewModel.respondSchedule(scheduleUid, response);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsHelper.handlePermissionResult(requestCode, grantResults);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setScheduleViewRespondViewModel(ScheduleViewRespondViewModel scheduleViewRespondViewModel) {
        this.scheduleViewRespondViewModel = scheduleViewRespondViewModel;
    }
}