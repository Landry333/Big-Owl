package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.activity.LoginPageActivity;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.service.SupervisorSchedulesAlarmReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this class is to set/define alarms for member schedules that the app will set.
 * After the time activation of an alarm, code will be executed from
 * {@link SupervisorSchedulesAlarmReceiver}
 */
public class SupervisorSchedulesAlarmManager {


    private final Context context;
    private final ScheduleRepository scheduleRepository;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SupervisorSchedulesAlarmManager(Context context) {

        this.context = context;
        scheduleRepository = new ScheduleRepository();
    }

    /**
     * Sets the alarm(s) for the SupervisorSchedulesAlarmReceiver given the coming schedules that the user is supervising
     *
     * @param userID The Id of the user who is a supervisor of some schedules
     */
    public void setAlarms(String userID) {
        int requestCode = 0;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        getSchedulesForSupervisor(userID)
                .addOnSuccessListener(scheduleList -> {
                    Toast.makeText(context, "Found supervisor schedules and started setAlarms", Toast.LENGTH_LONG).show();
                    for (Schedule schedule : scheduleList) {
                        Log.e("found schedule", schedule.toString());

                        Intent intent = new Intent(context, SupervisorSchedulesAlarmReceiver.class);

                        intent.putExtra("scheduleUid", schedule.getUid());
                        Log.e("ScheduleId in Intent", schedule.getUid());
                        Log.e("supervisor Intent", intent.toString());
                        //intent.putExtra(EXTRA_LATITUDE, schedule.getLocation().getLatitude());
                        //intent.putExtra(EXTRA_LONGITUDE, schedule.getLocation().getLongitude());
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                addMinutesToDate(schedule.getStartTime().toDate(),12).getTime(), pendingIntent);
                        Log.e("supervisor pendingIntent", pendingIntent.toString());
                    }
                }).addOnFailureListener(error -> {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("addOnFailureListener Error", error.getMessage());
        });
    }
    public Date addMinutesToDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
    /**
     * Obtains the list of schedules that the user is supervising, and filters in schedules that have been accepted
     *
     * @param userID The Id of the user
     * @return A Task that contains a list of schedule for the user that hasn't been attended
     */

    private Task<List<Schedule>> getSchedulesForSupervisor(String userID) {
        return scheduleRepository.getTaskListSchedulesForSupervisor(userID).addOnFailureListener(error -> {
            Log.e("addOnFailureListener Error1", error.getMessage());})
                .onSuccessTask(tDocs -> {
                    List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                    List<Schedule> acceptedScheduleList = scheduleList.stream()
                            //.filter(schedule -> schedule.getUserScheduleResponseMap().get(userID).getResponse() == Response.ACCEPT)
                            .collect(Collectors.toList());
                    return Tasks.forResult(acceptedScheduleList);
                }).addOnFailureListener(error -> {
                            Log.e("addOnFailureListener Error2", error.getMessage());});

    }

    public Task<QuerySnapshot> getTaskListSchedulesForSupervisor(String userID) {
        return db.collection("schedules").whereEqualTo("groupSupervisorUid", userID)
                .whereGreaterThanOrEqualTo("startTime", Timestamp.now())
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get();
    }

    private Task<List<Schedule>> getSchedulesForUser(String userID) {
        return scheduleRepository.getTaskListSchedulesForUser(userID)
                .onSuccessTask(tDocs -> {
                    List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                    List<Schedule> acceptedScheduleList = scheduleList.stream()
                            .filter(schedule -> schedule.getUserScheduleResponseMap().get(userID).getResponse() == Response.ACCEPT)
                            .collect(Collectors.toList());
                    return Tasks.forResult(acceptedScheduleList);
                });
    }

    /*private Task<List<Schedule>> getSchedulesForSupervisor(String userID) {
        return scheduleRepository.getTaskListSchedulesForSupervisor(userID)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                        List<Schedule> acceptedScheduleList = new ArrayList<>();
                        for (Schedule schedule : scheduleList) {
                            if (schedule.getGroupSupervisorUid().equalsIgnoreCase(userID)) {
                            //if (schedule.getUserScheduleResponseMap()
                                    .get(userID).getResponse() == Response.ACCEPT) {//
                                acceptedScheduleList.add(schedule);
                            }
                        }
                        return Tasks.forResult(acceptedScheduleList);
                    } else {
                        throw task.getException();
                    }

                });
    }*/
}
