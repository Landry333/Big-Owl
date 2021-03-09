package com.example.bigowlapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.ScheduleRepository;
<<<<<<< HEAD:app/src/main/java/com/example/bigowlapp/utils/SupervisorSchedulesAlarmManager.java
import com.example.bigowlapp.service.SupervisorSchedulesAlarmReceiver;
=======
import com.example.bigowlapp.service.MemberScheduleAlarmReceiver;
>>>>>>> cb08e49b234cf74f58972ebf5d0eaf15a21991c0:app/src/main/java/com/example/bigowlapp/utils/MemberScheduleAlarmManager.java
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

<<<<<<< HEAD:app/src/main/java/com/example/bigowlapp/utils/SupervisorSchedulesAlarmManager.java
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
=======
>>>>>>> cb08e49b234cf74f58972ebf5d0eaf15a21991c0:app/src/main/java/com/example/bigowlapp/utils/MemberScheduleAlarmManager.java
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this class is to set/define alarms for member schedules that the app will set.
 * After the time activation of an alarm, code will be executed from
<<<<<<< HEAD:app/src/main/java/com/example/bigowlapp/utils/SupervisorSchedulesAlarmManager.java
 * {@link SupervisorSchedulesAlarmReceiver}
 */
public class SupervisorSchedulesAlarmManager {
=======
 * {@link MemberScheduleAlarmReceiver}
 */
public class MemberScheduleAlarmManager {
>>>>>>> cb08e49b234cf74f58972ebf5d0eaf15a21991c0:app/src/main/java/com/example/bigowlapp/utils/MemberScheduleAlarmManager.java

    private final Context context;
    private final ScheduleRepository scheduleRepository;

<<<<<<< HEAD:app/src/main/java/com/example/bigowlapp/utils/SupervisorSchedulesAlarmManager.java
    public SupervisorSchedulesAlarmManager(Context context) {
=======
    public MemberScheduleAlarmManager(Context context) {
>>>>>>> cb08e49b234cf74f58972ebf5d0eaf15a21991c0:app/src/main/java/com/example/bigowlapp/utils/MemberScheduleAlarmManager.java
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
<<<<<<< HEAD:app/src/main/java/com/example/bigowlapp/utils/SupervisorSchedulesAlarmManager.java
                        Intent intent = new Intent(context, SupervisorSchedulesAlarmReceiver.class);
=======
                        Intent intent = new Intent(context, MemberScheduleAlarmReceiver.class);
>>>>>>> cb08e49b234cf74f58972ebf5d0eaf15a21991c0:app/src/main/java/com/example/bigowlapp/utils/MemberScheduleAlarmManager.java
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
<<<<<<< HEAD:app/src/main/java/com/example/bigowlapp/utils/SupervisorSchedulesAlarmManager.java
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

=======
        return scheduleRepository.getTaskListSchedulesForUser(userID)
                .onSuccessTask(tDocs -> {
                    List<Schedule> scheduleList = tDocs.toObjects(Schedule.class);
                    List<Schedule> acceptedScheduleList = scheduleList.stream()
                            .filter(schedule -> schedule.getUserScheduleResponseMap().get(userID).getResponse() == Response.ACCEPT)
                            .collect(Collectors.toList());
                    return Tasks.forResult(acceptedScheduleList);
>>>>>>> cb08e49b234cf74f58972ebf5d0eaf15a21991c0:app/src/main/java/com/example/bigowlapp/utils/MemberScheduleAlarmManager.java
                });
    }
}
