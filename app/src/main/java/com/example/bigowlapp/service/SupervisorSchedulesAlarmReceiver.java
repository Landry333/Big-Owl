package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.LiveDataWithStatus;
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
    SmsSender smsSender;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Execute Schedule activatedSchedule = getSchedule(intent); to get the schedule
        // Run the location/Geofencing code
        // activatedSchedule should only have UID, LONGITUDE, LATITUDE
        Schedule activatedSchedule = getSchedule(intent);
        LiveDataWithStatus<Schedule> supervisorScheduleData = repositoryFacade.getScheduleRepository()
                .getDocumentByUid(activatedSchedule.getUid(), Schedule.class);
        supervisorScheduleData.observe((LifecycleOwner) this, supervisorSchedule -> {
            if (supervisorSchedule == null) {
                return;
            } else {
                for (String scheduleMemberUid : supervisorSchedule.getMemberList()) {
                    if (supervisorSchedule.getUserScheduleResponseMap().get(scheduleMemberUid)
                            .getAttendance().getScheduleLocated() == Attendance.LocatedStatus.NOT_DETECTED) {
                        String scheduleMemberPhoneNumber = repositoryFacade.getUserRepository()
                                .getDocumentByUid(scheduleMemberUid, User.class)
                                .getValue().getPhoneNumber();
                        smsSender.sendSMS(context, scheduleMemberPhoneNumber,
                                "ATTENTION: This is a message from BigOwl attendance system." +
                                        "BigOwl wasn't able to get your current location for" +
                                        " your next schedule. Please make sure your internet and the " +
                                        "BigOwl mobile application are turned on");
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
