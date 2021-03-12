package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.SmsSender;
import com.example.bigowlapp.utils.SupervisorSchedulesAlarmManager;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this BroadcastReceiver is to execute code after the time activation of an alarm.
 * The alarms are set/defined in
 * {@link SupervisorSchedulesAlarmManager}
 */
public class SupervisorSchedulesAlarmReceiver extends BroadcastReceiver {

    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();
    SmsSender smsSender = new SmsSender();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Execute Schedule activatedSchedule = getSchedule(intent); to get the schedule
        // Run the location/Geofencing code
        // activatedSchedule should only have UID, LONGITUDE, LATITUDE
        Toast.makeText(context, "Received Alarm and started to send sms", Toast.LENGTH_LONG).show();
        String scheduleId = intent.getStringExtra("scheduleUid");
        //Toast.makeText(context, "scheduleId from intent: "+scheduleId, Toast.LENGTH_LONG).show();
        //String scheduleID = getIntent().getStringExtra(Intent.EXTRA_UID);
        //Schedule activatedSchedule = getSchedule(intent);
        Log.e("scheduleId from intent", scheduleId);

        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(supervisorSchedule -> {
                    if (supervisorSchedule == null) {
                        return;
                    } else {
                        for (String scheduleMemberUid : supervisorSchedule.getMemberList()) {
                            if (supervisorSchedule.getUserScheduleResponseMap().get(scheduleMemberUid)
                                    .getAttendance().getScheduleLocated() == Attendance.LocatedStatus.NOT_DETECTED) {
                                repositoryFacade.getUserRepository()
                                        .getDocumentByUid(scheduleMemberUid, User.class)
                                        .observeForever(scheduleMember -> {
                                            Log.e("scheduleMember", scheduleMember.getFirstName());
                                            Log.e("scheduleMemberPhone", scheduleMember.getPhoneNumber());
                                            String scheduleMemberPhoneNum = scheduleMember.getPhoneNumber();
                                            smsSender.sendSMS(context, scheduleMemberPhoneNum,
                                                    "ATTENTION: BigOwl wasn't able to get your current location for" +
                                                            " your next attendance. Please make sure your internet and " +
                                                            "BigOwl app are on");

                                        });

                            }
                        }
                    }
                });

    }

    private Schedule getSchedule(Intent intent) {
        //double defaultValueLatLng = 0.0;
        Schedule schedule = new Schedule();
        schedule.setUid(intent.getStringExtra(EXTRA_UID));
        /*GeoPoint geoPoint = new GeoPoint(intent.getDoubleExtra(EXTRA_LATITUDE, defaultValueLatLng),
                intent.getDoubleExtra(EXTRA_LONGITUDE, defaultValueLatLng));
        schedule.setLocation(geoPoint);*/
        return schedule;
    }

}
