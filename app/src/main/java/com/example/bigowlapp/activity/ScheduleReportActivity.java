package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.ScheduleReportMembersAdapter;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.utils.GeoLocationFormatter;
import com.example.bigowlapp.viewModel.ScheduleReportViewModel;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

public class ScheduleReportActivity extends BigOwlActivity {
    private String scheduleUid, supervisorId;
    private TextView scheduleReportTitle;
    private TextView scheduleReportStartTime;
    private TextView scheduleReportEndTime;
    private TextView scheduleReportLocation;
    private ScheduleReportViewModel scheduleReportViewModel;
    private ListView scheduleReportMemberListView;
    private ScheduleReportMembersAdapter scheduleReportMembersAdapter;
    private GeoLocationFormatter geoLocationFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleUid = getIntent().getStringExtra("scheduleUid");
        supervisorId = getIntent().getStringExtra("supervisorId");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (scheduleReportViewModel == null)
            scheduleReportViewModel = new ViewModelProvider(this).get(ScheduleReportViewModel.class);
        if (geoLocationFormatter == null)
            geoLocationFormatter = new GeoLocationFormatter();

        subscribeToData();
    }

    private void subscribeToData() {
        if (!scheduleReportViewModel.isCurrentUserSupervisor(supervisorId)) {
            return;
        }

        scheduleReportViewModel.getCurrentScheduleData(scheduleUid).observe(this, schedule -> {
            if (schedule == null) {
                return;
            }
            scheduleReportTitle = findViewById(R.id.schedule_report_title);
            scheduleReportStartTime = findViewById(R.id.schedule_report_start_time);
            scheduleReportEndTime = findViewById(R.id.schedule_report_end_time);
            scheduleReportLocation = findViewById(R.id.schedule_report_location);
            scheduleReportMemberListView = findViewById(R.id.schedule_report_member_list);

            scheduleReportTitle.setText(schedule.getTitle());
            scheduleReportStartTime.setText(schedule.getStartTime().toDate().toString());
            scheduleReportEndTime.setText(schedule.getEndTime().toDate().toString());

            if (geoLocationFormatter == null) {
                geoLocationFormatter = new GeoLocationFormatter();
            }

            scheduleReportLocation.setText(geoLocationFormatter.formatLocation(this, schedule.getLocation()));

            scheduleReportViewModel.getScheduleMemberNameMap(schedule.getMemberList()).observe(this, memberNameMap -> {
                if (schedule.scheduleCurrentState() == Schedule.Status.ON_GOING)
                    Toast.makeText(this, "Schedule is ongoing and attendance results are subject to change", Toast.LENGTH_LONG).show();

                scheduleReportMembersAdapter = new ScheduleReportMembersAdapter(this, memberNameMap, schedule);
                scheduleReportMemberListView.setAdapter(scheduleReportMembersAdapter);
            });
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_schedule_report;
    }


    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setScheduleReportViewModel(ScheduleReportViewModel scheduleReportViewModel) {
        this.scheduleReportViewModel = scheduleReportViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public ScheduleReportMembersAdapter getScheduleReportMembersAdapter() {
        return scheduleReportMembersAdapter;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setGeoLocationFormatter(GeoLocationFormatter geoLocationFormatter) {
        this.geoLocationFormatter = geoLocationFormatter;
    }
}
