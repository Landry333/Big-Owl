package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.service.SupervisorSchedulesAlarmReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to set/define alarms for supervisor schedules that the app will set.
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
     * Sets the alarm(s) for the SupervisorSchedulesAlarmReceiver given the coming schedules that the user is supervising
     *
     * @param userID The Id of the user who is a supervisor of some schedules
     */
    public void setAlarms(String userID) {
        int requestCode = 0;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        getSchedulesForSupervisor(userID)
                .addOnSuccessListener(scheduleList -> {
                    for (Schedule schedule : scheduleList) {
                        Intent intent = new Intent(context, SupervisorSchedulesAlarmReceiver.class);
                        intent.putExtra("scheduleUid", schedule.getUid());
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                addMinutesToDate(schedule.getStartTime().toDate(), 4).getTime(), pendingIntent);
                    }
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
     * @param userID The Id of the supervisor
     * @return A Task that contains a list of schedules for the supervisor that haven't yet passed
     */

    private Task<List<Schedule>> getSchedulesForSupervisor(String userID) {
        return scheduleRepository.getTaskListSchedulesForSupervisor(userID)
                .onSuccessTask(tDocs -> {
                    List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                    List<Schedule> acceptedScheduleList = scheduleList.stream()
                            .collect(Collectors.toList());
                    return Tasks.forResult(acceptedScheduleList);
                });

    }

}
