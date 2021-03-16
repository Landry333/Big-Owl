package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.ScheduleReportMembersAdapter;
import com.example.bigowlapp.viewModel.ScheduleReportViewModel;

import androidx.lifecycle.ViewModelProvider;

public class ScheduleReportActivity extends BigOwlActivity {
    private String scheduleUid, supervisorId;
    private TextView scheduleReportTitle, scheduleReportStartTime, scheduleReportEndTime, scheduleReportLocation;
    private ScheduleReportViewModel scheduleReportViewModel;
    private ListView scheduleReportMemberListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleUid = getIntent().getStringExtra("scheduleUid");
        supervisorId = getIntent().getStringExtra("supervisorId");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (scheduleReportViewModel == null) {
            scheduleReportViewModel = new ViewModelProvider(this).get(ScheduleReportViewModel.class);
        }

        subscribeToData();
    }

    private void subscribeToData() {
        if (!scheduleReportViewModel.isCurrentUserSupervisor(supervisorId)) {
            return;
        }

        scheduleReportViewModel.getCurrentScheduleData(scheduleUid).observe(this, schedule -> {
            if (schedule != null) {
                scheduleReportTitle = findViewById(R.id.schedule_report_title);
                scheduleReportStartTime = findViewById(R.id.schedule_report_start_time);
                scheduleReportEndTime = findViewById(R.id.schedule_report_end_time);
                scheduleReportLocation = findViewById(R.id.schedule_report_location);
                scheduleReportMemberListView = findViewById(R.id.schedule_report_member_list);

                scheduleReportTitle.setText(schedule.getTitle());
                scheduleReportStartTime.setText(schedule.getStartTime().toDate().toString());
                scheduleReportEndTime.setText(schedule.getEndTime().toDate().toString());
                scheduleReportLocation.setText(scheduleReportViewModel.getScheduleLocation(this, schedule));
                scheduleReportMemberListView.setAdapter(new ScheduleReportMembersAdapter(
                        this, scheduleReportViewModel.getMemberNameDidAttendMap(schedule)));
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_schedule_report;
    }

}
