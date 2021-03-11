package com.example.bigowlapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticatorByDeviceId {

    private String deviceIdNumber;
    private TextView deviceIdDisplay;
    private TextView authenticationStatusDisplay;

    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    private final Context context;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AuthenticatorByDeviceId(Context context) {
        this.context = context;
    }

    /*public String getDeviceIdNumber() {
        return deviceIdNumber;
    }*/

    @SuppressLint("MissingPermission")
    public void authenticate(String scheduleId) {
        //String scheduleId = getIntent().getStringExtra("scheduleId");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        //deviceIdDisplay = findViewById(R.id.device_Id_Display_2);
        //deviceIdDisplay.setText("Device ID Number: " + deviceIdNumber);

        //authenticationStatusDisplay = findViewById(R.id.authentication_Status_Display_2);
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(schedule -> {
                    UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                            .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                    Attendance attendance = userScheduleResponse.getAttendance();
                    String savedDeviceId = attendance.getDeviceIdNumber();
                    Attendance.LocatedStatus scheduleLocated = attendance.getScheduleLocated();

                    if (scheduleLocated == Attendance.LocatedStatus.CORRECT_LOCATION && deviceIdNumber.equalsIgnoreCase(savedDeviceId)) {
                        //authenticationStatusDisplay.setText("Authentication Status: SUCCEEDED");
                        Toast.makeText(context, "Big Owl next schedule authentication SUCCEEDED", Toast.LENGTH_LONG).show();
                        attendance.setAuthenticated(true);
                    } else {
                        //authenticationStatusDisplay.setText("Authentication Status: FAILED");
                        attendance.setAuthenticated(false);
                        Toast.makeText(context, "Big Owl next schedule authentication FAILED", Toast.LENGTH_LONG).show();
                    }
                    attendance.setAttemptedAuthByPhoneUid(true);
                    repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                });
    }



}
