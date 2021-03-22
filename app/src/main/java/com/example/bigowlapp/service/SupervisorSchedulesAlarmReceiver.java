package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.SmsSender;
import com.example.bigowlapp.utils.SupervisorSchedulesAlarmManager;

/**
 * The purpose of this BroadcastReceiver is to execute code after the time activation of an alarm.
 * The alarms are set/defined in
 * {@link SupervisorSchedulesAlarmManager}
 */
public class SupervisorSchedulesAlarmReceiver extends BroadcastReceiver {

    private RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();
    private static final String WARNING_MESSAGE = "ATTENTION: BigOwl wasn't able to detect your current location for" +
            " your next attendance. Please make sure your internet is on and " +
            " that app are signed in";

    @Override
    public void onReceive(Context context, Intent intent) {
        String scheduleId = intent.getStringExtra("scheduleUid");
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(supervisorSchedule -> {
                    if (supervisorSchedule == null) {
                        return;
                    } else {
                        for (String scheduleMemberUid : supervisorSchedule.getMemberList()) {
                            if ((supervisorSchedule.getUserScheduleResponseMap().get(scheduleMemberUid)
                                    .getAttendance().getScheduleLocated() == Attendance.LocatedStatus.NOT_DETECTED) && supervisorSchedule.getUserScheduleResponseMap().get(scheduleMemberUid)
                                    .getAttendance().isAuthenticated() == false && (supervisorSchedule.getUserScheduleResponseMap().get(scheduleMemberUid).getResponse()==Response.ACCEPT)) {
                                repositoryFacade.getUserRepository()
                                        .getDocumentByUid(scheduleMemberUid, User.class)
                                        .observeForever(scheduleMember -> {
                                            String scheduleMemberPhoneNum = scheduleMember.getPhoneNumber();
                                            SmsSender.sendSMS(scheduleMemberPhoneNum,WARNING_MESSAGE);
                                        });

                            }
                        }
                    }
                });

    }
}
