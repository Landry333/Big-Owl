package com.example.bigowlapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.installations.FirebaseInstallations;

public class AuthenticatorByAppInstanceId {

    private String appInstanceId;
    private final Context context;
    private RepositoryFacade repositoryFacade;

    public AuthenticatorByAppInstanceId(Context context) {
        this.context = context;
        this.repositoryFacade = RepositoryFacade.getInstance();
    }

    public void authenticate(String scheduleId) {
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(schedule -> {
                    UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                            .get(repositoryFacade.getCurrentUserUid());
                    Attendance attendance = userScheduleResponse.getAttendance();
                    String savedAppInstanceId = attendance.getAppInstanceId();
                    appInstanceId = FirebaseInstallations.getInstance().getId().getResult();
                    if (appInstanceId.equalsIgnoreCase(savedAppInstanceId)) {
                        Toast.makeText(context, "SUCCESS in authentication for your next BIG OWL schedule", Toast.LENGTH_LONG).show();
                        attendance.setAuthenticated(true);
                    } else {
                        attendance.setAuthenticated(false);
                        Toast.makeText(context, "FAILURE in authentication for your next BIG OWL schedule", Toast.LENGTH_LONG).show();
                    }
                    attendance.setAttemptedAuthByAppUid(true);
                    repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                });
    }

}
