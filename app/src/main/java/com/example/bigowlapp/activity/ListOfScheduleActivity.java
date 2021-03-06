package com.example.bigowlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.view_model.ScheduleListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListOfScheduleActivity extends BigOwlActivity {
    private ListView scheduleListView;
    private ScheduleListViewModel scheduleListViewModel;
    private String groupID;
    private String groupName;
    private String supervisorName;
    private String supervisorId;
    private Intent intentToSchedule;
    private boolean isUserTheGroupSupervisor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getStringExtra("groupID");
        groupName = getIntent().getStringExtra("groupName");
        supervisorName = getIntent().getStringExtra("supervisorName");
        isUserTheGroupSupervisor = getIntent().getBooleanExtra("isUserTheGroupSupervisor", false);
        supervisorId = getIntent().getStringExtra("supervisorId");
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_list_of_schedule;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (scheduleListViewModel == null) {
            scheduleListViewModel = new ViewModelProvider(this).get(ScheduleListViewModel.class);
        }
        subscribeToData();
    }

    private void subscribeToData() {
        if (!scheduleListViewModel.isCurrentUserSet()){
            return;
        }

        scheduleListViewModel.getScheduleList(isUserTheGroupSupervisor, groupID).observe(this, schedules -> {
            if (schedules != null) {
                scheduleListView = findViewById(R.id.schedule_list);
                scheduleListView.setAdapter(new ScheduleAdapter(getBaseContext(), new ArrayList<>(schedules)));

                scheduleListView.setOnItemClickListener((arg0, v, position, arg3) -> {

                    if (isUserTheGroupSupervisor) {
                        intentToSchedule = new Intent(getBaseContext(), ScheduleReportActivity.class);
                        intentToSchedule.putExtra("scheduleUid", schedules.get(position).getUid());
                        intentToSchedule.putExtra("supervisorId", supervisorId);
                    } else {
                        intentToSchedule = new Intent(getBaseContext(), ScheduleViewRespondActivity.class);
                        intentToSchedule.putExtra("scheduleUid", schedules.get(position).getUid());
                        intentToSchedule.putExtra("groupID", groupID);
                        intentToSchedule.putExtra("groupName", groupName);
                        intentToSchedule.putExtra("supervisorName", supervisorName);
                    }
                    startActivity(intentToSchedule);
                });
            } else {
                this.noScheduleAlert().show();
            }
        });
    }

    private static class ScheduleAdapter extends ArrayAdapter<Schedule> {

        public ScheduleAdapter(@NonNull Context context, List<Schedule> schedules) {
            super(context, 0, schedules);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Schedule schedule = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.fragment_schedule_list_item, parent, false);
            }
            TextView scheduleTitle = convertView.findViewById(R.id.text_view_schedule_title);
            TextView scheduleTime = convertView.findViewById(R.id.text_view_schedule_start_time);

            scheduleTitle.setText(schedule.getTitle());
            scheduleTime.setText(schedule.getStartTime().toDate().toString());
            return convertView;
        }
    }

    public AlertDialog noScheduleAlert() {
        return new AlertDialog.Builder(this)
        .setTitle("No schedule found!")
        .setMessage(isUserTheGroupSupervisor ? "You have never set a schedule" : "You currently have no future schedules")
        .setPositiveButton("Ok", (dialogInterface, which) -> ListOfScheduleActivity.super.onBackPressed())
        .setCancelable(false)
        .create();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setScheduleListViewModel(ScheduleListViewModel scheduleListViewModel) {
        this.scheduleListViewModel = scheduleListViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Intent getIntentToScheduleForTest() {
        return this.intentToSchedule;
    }

}