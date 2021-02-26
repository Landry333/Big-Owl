package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.service.AlarmBroadcastReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AlarmBroadcastReceiverManager {

    private final Context context;
    private ScheduleRepository scheduleRepository;

    public AlarmBroadcastReceiverManager(Context context) {
        this.context = context;
        scheduleRepository = new ScheduleRepository();
    }

    public void setAlarm(String userID) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        getSchedulesForUser(userID)
                .addOnSuccessListener(scheduleList -> {
                    for (Schedule schedule : scheduleList) {
                        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
                        intent.putExtra("Uid", schedule.getUid());
                        intent.putExtra("Latitude", schedule.getLocation().getLatitude());
                        intent.putExtra("Longitude", schedule.getLocation().getLongitude());
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                schedule.getStartTime().toDate().getTime(), pendingIntent);
                    }
                }).addOnFailureListener(error -> {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private Task<List<Schedule>> getSchedulesForUser(String userID) {
        return scheduleRepository.getTaskListSchedulesForUser(userID)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                        List<Schedule> acceptedScheduleList = new ArrayList<>();
                        for (Schedule schedule : scheduleList) {
                            if (schedule.getUserScheduleResponseMap().get(userID).getResponse() == Response.ACCEPT) {
                                acceptedScheduleList.add(schedule);
                            }
                        }
                        return Tasks.forResult(acceptedScheduleList);
                    } else {
                        throw task.getException();
                    }

                });
    }
}
