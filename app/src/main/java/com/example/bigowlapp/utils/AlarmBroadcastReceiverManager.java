package com.example.bigowlapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AlarmBroadcastReceiverManager {

    private final Context context;
    private ScheduleRepository scheduleRepository;

    public AlarmBroadcastReceiverManager(Context context) {
        this.context = context;
        scheduleRepository = new ScheduleRepository();
    }

    public void setAlarm() {
    }

    public Task<List<Schedule>> getSchedulesForUser(String userID) {
        return scheduleRepository.getTaskListSchedulesForUser(userID)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                        return Tasks.forResult(scheduleList);
                    } else {
                        Toast.makeText(context, task.getException().getMessage().toString(),
                                Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }

                });
    }
}
