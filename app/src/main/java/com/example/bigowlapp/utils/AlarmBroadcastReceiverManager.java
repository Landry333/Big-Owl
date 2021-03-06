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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LATITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LONGITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this class is to set/define alarms that the app will set.
 * After the time activation of an alarm, code will be executed from
 * {@link com.example.bigowlapp.service.AlarmBroadcastReceiver}
 */
public class AlarmBroadcastReceiverManager {

    private final Context context;
    private final ScheduleRepository scheduleRepository;

    public AlarmBroadcastReceiverManager(Context context) {
        this.context = context;
        scheduleRepository = new ScheduleRepository();
    }

    // TODO: --START-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.
    public AlarmBroadcastReceiverManager(Context context, ScheduleRepository scheduleRepository) {
        this.context = context;
        this.scheduleRepository = scheduleRepository;
    }
    // TODO: --END-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.

    /**
     * Sets the alarm(s) for the BroadcastReceiver given the schedules that the user has
     *
     * @param userID The Id of the user
     */
    public void setAlarms(String userID) {
        int requestCode = 0;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        getSchedulesForUser(userID)
                .addOnSuccessListener(scheduleList -> {
                    for (Schedule schedule : scheduleList) {
                        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
                        intent.putExtra(EXTRA_UID, schedule.getUid());
                        intent.putExtra(EXTRA_LATITUDE, schedule.getLocation().getLatitude());
                        intent.putExtra(EXTRA_LONGITUDE, schedule.getLocation().getLongitude());
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                schedule.getStartTime().toDate().getTime(), pendingIntent);
                    }
                }).addOnFailureListener(error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Obtains the list of schedules for the user, and filters in schedules that have been accepted
     *
     * @param userID The Id of the user
     * @return A Task that contains a list of schedule for the user that hasn't been attended
     */
    private Task<List<Schedule>> getSchedulesForUser(String userID) {

        // TODO: --START-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.
        if (userID.equals("testing")) {
            List<Schedule> acceptedScheduleList = new ArrayList<>();

            Schedule schedule = new Schedule();
            schedule.setUid("Hello there");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, 30);
            Timestamp timestamp = new Timestamp(calendar.getTime());
            schedule.setStartTime(timestamp);
            GeoPoint geoPoint = new GeoPoint(10, -82);
            schedule.setLocation(geoPoint);

            acceptedScheduleList.add(schedule);

            return Tasks.forResult(acceptedScheduleList);
        }
        // TODO: --END-- REMOVE BEFORE MERGING PULL REQUEST. THIS IS ONLY FOR TESTING PURPOSES.

        return scheduleRepository.getTaskListSchedulesForUser(userID)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                        List<Schedule> acceptedScheduleList = new ArrayList<>();
                        acceptedScheduleList = scheduleList.stream()
                                .filter(schedule -> schedule.getUserScheduleResponseMap().get(userID).getResponse() == Response.ACCEPT)
                                .collect(Collectors.toList());
                        return Tasks.forResult(acceptedScheduleList);
                    } else {
                        throw task.getException();
                    }

                });
    }
}
