package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.utils.GeoLocationFormatter;
import com.example.bigowlapp.utils.PermissionsHelper;
import com.example.bigowlapp.viewModel.ScheduleViewRespondViewModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

public class ScheduleViewRespondActivity extends BigOwlActivity {

    private String scheduleUid;
    private String groupName;
    private String supervisorName;
    private ScheduleViewRespondViewModel scheduleViewRespondViewModel;
    private PermissionsHelper permissionsHelper;
    private GeoLocationFormatter geoLocationFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        scheduleUid = intent.getStringExtra("scheduleUid");
        groupName = intent.getStringExtra("groupName");
        supervisorName = intent.getStringExtra("supervisorName");

        this.permissionsHelper = new PermissionsHelper(this);
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
            ((TextView) findViewById(R.id.text_view_schedule_title)).setText(schedule.getTitle());
            ((TextView) findViewById(R.id.text_view_group_supervisor_name)).setText(supervisorName);
            ((TextView) findViewById(R.id.text_view_schedule_start_time)).setText(formatTime(schedule.getStartTime().toDate()));
            ((TextView) findViewById(R.id.text_view_schedule_end_time)).setText(formatTime(schedule.getEndTime().toDate()));

            if (geoLocationFormatter == null)
                geoLocationFormatter = new GeoLocationFormatter();

            ((TextView) findViewById(R.id.text_view_schedule_location)).setText(geoLocationFormatter.formatLocation(this, schedule.getLocation()));

            UserScheduleResponse userScheduleResponse = scheduleViewRespondViewModel.getUserScheduleResponse();
            if (userScheduleResponse.getResponse() != Response.NEUTRAL) {
                findViewById(R.id.linear_layout_system_response).setVisibility(View.VISIBLE);
                findViewById(R.id.linear_layout_member_schedule_response).setVisibility(View.VISIBLE);
                findViewById(R.id.line_below_system_response).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.text_view_schedule_user_response_text))
                        .setText(userScheduleResponse.getResponse() == Response.ACCEPT ? "Accepted" : "Rejected");
                ((TextView) findViewById(R.id.text_view_schedule_user_response_time))
                        .setText(userScheduleResponse.getResponseTime().toDate().toString());
            } else {
                findViewById(R.id.linear_layout_system_response).setVisibility(View.GONE);
                findViewById(R.id.linear_layout_member_schedule_response).setVisibility(View.GONE);
                findViewById(R.id.line_below_system_response).setVisibility(View.GONE);
            }
            if (schedule.scheduleCurrentState() != Schedule.Status.SCHEDULED) {
                findViewById(R.id.linear_layout_system_response).setVisibility(View.VISIBLE);
                Map<String, Object> map = schedule.scheduleMemberResponseAttendanceMap(scheduleViewRespondViewModel.getCurrentUserUid());
                ((TextView) findViewById(R.id.text_view_schedule_member_attendance))
                        .setText((String) map.get("responseText"));
                ((TextView) findViewById(R.id.text_view_schedule_member_attendance))
                        .setTextColor((int) map.get("responseColor"));
                setResponseButtonsVisibility(null);
            } else {
                findViewById(R.id.layout_member_attendance_result).setVisibility(View.GONE);
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

    private String formatTime(Date time) {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(time);
    }

    public void requestLocationPermissions() {
        permissionsHelper.requestLocationAndBackgroundLocationPermissions();
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

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setGeoLocationFormatter(GeoLocationFormatter geoLocationFormatter) {
        this.geoLocationFormatter = geoLocationFormatter;
    }
}