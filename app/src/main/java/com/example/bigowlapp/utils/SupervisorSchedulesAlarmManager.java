package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.service.SupervisorSchedulesAlarmReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this class is to set/define alarms that the app will set.
 * After the time activation of an alarm, code will be executed from
 * {@link SupervisorSchedulesAlarmReceiver}
 */
public class SupervisorSchedulesAlarmManager {

    private final Context context;
    private final ScheduleRepository scheduleRepository;

    public SupervisorSchedulesAlarmManager(Context context) {
        this.context = context;
        scheduleRepository = new ScheduleRepository();
    }

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
                        Intent intent = new Intent(context, SupervisorSchedulesAlarmReceiver.class);
                        intent.putExtra(EXTRA_UID, schedule.getUid());
                        //intent.putExtra(EXTRA_LATITUDE, schedule.getLocation().getLatitude());
                        //intent.putExtra(EXTRA_LONGITUDE, schedule.getLocation().getLongitude());
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                addMinutesToDate(schedule.getStartTime().toDate(),12).getTime(), pendingIntent);
                    }
                }).addOnFailureListener(error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
    }
    public Date addMinutesToDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
    /**
     * Obtains the list of schedules for the user, and filters in schedules that have been accepted
     *
     * @param userID The Id of the user
     * @return A Task that contains a list of schedule for the user that hasn't been attended
     */
    private Task<List<Schedule>> getSchedulesForUser(String userID) {
        return scheduleRepository.getTaskListSchedulesForSupervisor(userID)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                        List<Schedule> acceptedScheduleList = new ArrayList<>();
                        for (Schedule schedule : scheduleList) {
                            if (schedule.getGroupSupervisorUid().equalsIgnoreCase(userID)){
                            /*if (schedule.getUserScheduleResponseMap()
                                    .get(userID).getResponse() == Response.ACCEPT) {*/
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
