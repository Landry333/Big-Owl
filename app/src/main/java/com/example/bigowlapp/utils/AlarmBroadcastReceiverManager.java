package com.example.bigowlapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;

import java.util.List;

public class AlarmBroadcastReceiverManager {

    private final Context context;
    private ScheduleRepository scheduleRepository;
    private List<Schedule> scheduleList;

    public AlarmBroadcastReceiverManager(Context context) {
        this.context = context;
        scheduleRepository = new ScheduleRepository();
    }

    public void setAlarm() {

    }

    public void getSchedulesForUser(String userID) {
        scheduleRepository.getTaskListSchedulesForUser(userID)
                .addOnSuccessListener(task -> {
                    scheduleList = task.toObjects(Schedule.class);
                    for (Schedule schedule : scheduleList) {
                        Log.e("Bigowl", schedule.getStartTime() + " " + schedule.getUid());
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("Bigowl", error.getMessage());
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
