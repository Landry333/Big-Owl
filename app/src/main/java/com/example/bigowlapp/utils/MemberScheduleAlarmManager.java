package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.service.MemberScheduleAlarmReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LATITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LONGITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_SCHEDULE_TITLE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this class is to set/define alarms for member schedules that the app will set.
 * After the time activation of an alarm, code will be executed from
 * {@link MemberScheduleAlarmReceiver}
 */
public class MemberScheduleAlarmManager {

    private final Context context;
    private final ScheduleRepository scheduleRepository;

    public MemberScheduleAlarmManager(Context context) {
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
                        Intent intent = new Intent(context, MemberScheduleAlarmReceiver.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_UID, schedule.getUid());
                        bundle.putString(EXTRA_SCHEDULE_TITLE, schedule.getTitle());
                        bundle.putDouble(EXTRA_LATITUDE, schedule.getLocation().getLatitude());
                        bundle.putDouble(EXTRA_LONGITUDE, schedule.getLocation().getLongitude());
                        intent.putExtras(bundle);
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                schedule.getStartTime().toDate().getTime(), pendingIntent);
                    }
                });
    }

    /**
     * Obtains the list of schedules for the user, and filters in schedules that have been accepted
     *
     * @param userID The Id of the user
     * @return A Task that contains a list of schedule for the user that hasn't been attended
     */
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
}