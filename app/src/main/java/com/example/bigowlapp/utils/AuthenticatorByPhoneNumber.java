package com.example.bigowlapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.BigOwlActivity;
import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;

public class AuthenticatorByPhoneNumber {

    private String deviceIdNumber;
    private String devicePhoneNumber;
    private TextView deviceIdDisplay;
    private TextView authenticationStatusDisplay;
    private String currentUserPhoneNumber;
    //private String scheduleId = getIntent().getStringExtra("scheduleId");
    //private String scheduleId = "YiNpA4OWiR29iXPp1vHm";

    private final AuthRepository authRepository = new AuthRepository();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    private final Context context;

    public AuthenticatorByPhoneNumber(Context context) {
        this.context = context;
    }

    /*public String getDeviceIdNumber() {
        return deviceIdNumber;
    }*/


    //@RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    public void authenticate(String scheduleId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        devicePhoneNumber = telephonyManager.getLine1Number();
        //deviceIdDisplay.setText("Device ID Number: " + getDeviceIdNumber());

        //Attendance testAttendance = new Attendance();
        //testAttendance
        //UserScheduleResponse testUserScheduleResponse = new UserScheduleResponse();
        //testUserScheduleResponse.setAttendance(testAttendance);
        //Map<String, UserScheduleResponse> userScheduleResponseMap = Map.of("abcdefg",testUserScheduleResponse);
        //Map<String, UserScheduleResponse> userScheduleResponseMap = null;
        //userScheduleResponseMap.
        //Schedule testSchedule = new Schedule();
        //testSchedule.setUserScheduleResponseMap(userScheduleResponseMap);


        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observe((LifecycleOwner) context, schedule -> {
                    //repositoryFacade.getScheduleRepository().addDocument(testSchedule);
                    repositoryFacade.getUserRepository()
                            .getDocumentByUid(repositoryFacade.getAuthRepository().getCurrentUser().getUid(), User.class)
                            .observe((LifecycleOwner) context, user -> {
                                currentUserPhoneNumber = user.getPhoneNumber();
                                UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                                        .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                                Attendance attendance = userScheduleResponse.getAttendance();
                                Log.e(currentUserPhoneNumber, "currentUserPhoneNumber");
                                Log.e(devicePhoneNumber, "devicePhoneNumber");

                                if (currentUserPhoneNumber.equalsIgnoreCase("+"+devicePhoneNumber)) {
                                    //authenticationStatusDisplay.setText("Authentication Status: SUCCEEDED");
                                    attendance.setAuthenticated(true);
                                    Toast.makeText(context, "SUCCEEDED", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show();
                                    //authenticationStatusDisplay.setText("Authentication Status: FAILED");
                                    attendance.setAuthenticated(false);
                                    userScheduleResponse.getAttendance().setDeviceIdNumber(deviceIdNumber);
                                    AuthByPhoneNumberFailure authByPhoneNumberFailure = new AuthByPhoneNumberFailure();
                                    authByPhoneNumberFailure.setScheduleId(scheduleId);
                                    authByPhoneNumberFailure.setCreationTime(Timestamp.now());
                                    authByPhoneNumberFailure.setSenderPhoneNum(currentUserPhoneNumber);
                                    authByPhoneNumberFailure.setReceiverUid(schedule.getGroupSupervisorUid());
                                    authByPhoneNumberFailure.setSenderUid(authRepository.getCurrentUser().getUid());
                                    repositoryFacade.getNotificationRepository().addDocument(authByPhoneNumberFailure);
                                }
                                attendance.setAuthAttemptedUserMobileNumber(true);
                                repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                                //moveTaskToBack(true);
                            });
                });

    }

}
