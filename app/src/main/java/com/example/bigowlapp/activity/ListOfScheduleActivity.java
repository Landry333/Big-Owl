package com.example.bigowlapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.viewModel.ScheduleListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ListOfScheduleActivity extends AppCompatActivity {
    private ListView scheduleListView;
    private ScheduleListViewModel scheduleListViewModel;
    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_schedule);
        groupID = getIntent().getStringExtra("groupID");
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
        try {
            scheduleListViewModel.getScheduleList(groupID).observe(this, schedules -> {
                if (schedules != null) {
                    scheduleListView = findViewById(R.id.schedule_list);
                    scheduleListView.setAdapter(new ListOfScheduleActivity.ScheduleAdaptor(getBaseContext(), new ArrayList<>(schedules)));

                    scheduleListView.setOnItemClickListener((arg0, v, position, arg3) -> {
                        Intent intent = new Intent(getBaseContext(), ScheduleViewRespondActivity.class);
                        intent.putExtra("scheduleUId", schedules.get(position).getuId());
                        intent.putExtra("groupID", groupID);
                        startActivity(intent);
                        });
                } else {
                    this.noScheduleAlert().show();
                }
            });
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private class ScheduleAdaptor extends ArrayAdapter<Schedule> {

        public ScheduleAdaptor(@NonNull Context context, ArrayList<Schedule> schedules) {
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

    private AlertDialog noScheduleAlert() {
        return new AlertDialog.Builder(ListOfScheduleActivity.this)
                .setTitle("No schedule found!")
                .setMessage("You currently have no future schedules")
                .setPositiveButton("Ok", (dialogInterface, which) -> ListOfScheduleActivity.super.onBackPressed())
                .setCancelable(false)
                .create();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setSupervisedGroupListViewModel(ScheduleListViewModel scheduleListViewModel) {
        this.scheduleListViewModel = scheduleListViewModel;
    }
}