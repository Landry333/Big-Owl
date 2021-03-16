package com.example.bigowlapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticatorByDeviceId {

    private String deviceIdNumber;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    public AuthenticatorByDeviceId(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void authenticate(String scheduleId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(schedule -> {
                    UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                            .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                    Attendance attendance = userScheduleResponse.getAttendance();
                    String savedDeviceId = attendance.getDeviceIdNumber();
                    Attendance.LocatedStatus scheduleLocated = attendance.getScheduleLocated();

                    if (scheduleLocated == Attendance.LocatedStatus.CORRECT_LOCATION && deviceIdNumber.equalsIgnoreCase(savedDeviceId)) {
                        Toast.makeText(context, "SUCCESS in authentication for your next BIG OWL schedule", Toast.LENGTH_LONG).show();
                        attendance.setAuthenticated(true);
                    } else {
                        attendance.setAuthenticated(false);
                        Toast.makeText(context, "FAILURE in authentication for your next BIG OWL schedule", Toast.LENGTH_LONG).show();
                    }
                    attendance.setAttemptedAuthByPhoneUid(true);
                    repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                });
    }

}
