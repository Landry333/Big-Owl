package com.example.bigowlapp.activity;

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

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.AuthenticatorByPhoneNumber;
import com.google.firebase.Timestamp;

public class AuthenticationByPhoneNumberActivity extends BigOwlActivity {

    private String deviceIdNumber;
    private String devicePhoneNumber;
    private TextView deviceIdDisplay;
    private TextView authenticationStatusDisplay;
    private String currentUserPhoneNumber;
    private String scheduleId = "YiNpA4OWiR29iXPp1vHm";

    private final AuthRepository authRepository = new AuthRepository();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 10);
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_authentication_by_phone_number;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone State Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone State Permission DENIED", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    public String getDeviceIdNumber() {
        return deviceIdNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    protected void onStart() {
        super.onStart();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        devicePhoneNumber = telephonyManager.getLine1Number();
        deviceIdDisplay = findViewById(R.id.device_Id_Display_1);
        deviceIdDisplay.setText("Device ID Number: " + getDeviceIdNumber());
        AuthenticatorByPhoneNumber authenticationByPhoneNumber = new AuthenticatorByPhoneNumber(this);
        authenticationByPhoneNumber.authenticate("YiNpA4OWiR29iXPp1vHm");

        //Attendance testAttendance = new Attendance();
        //testAttendance
        //UserScheduleResponse testUserScheduleResponse = new UserScheduleResponse();
        //testUserScheduleResponse.setAttendance(testAttendance);
        //Map<String, UserScheduleResponse> userScheduleResponseMap = Map.of("abcdefg",testUserScheduleResponse);
        //Map<String, UserScheduleResponse> userScheduleResponseMap = null;
        //userScheduleResponseMap.
        //Schedule testSchedule = new Schedule();
        //testSchedule.setUserScheduleResponseMap(userScheduleResponseMap);


        authenticationStatusDisplay = findViewById(R.id.authentication_Status_Display_1);

        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observe(this, schedule -> {
                    //repositoryFacade.getScheduleRepository().addDocument(testSchedule);
                    repositoryFacade.getUserRepository()
                            .getDocumentByUid(repositoryFacade.getAuthRepository().getCurrentUser().getUid(), User.class)
                            .observe(this, user -> {
                                currentUserPhoneNumber = user.getPhoneNumber();
                                UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                                        .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                                Attendance attendance = userScheduleResponse.getAttendance();
                                Log.e(currentUserPhoneNumber, "currentUserPhoneNumber");
                                Log.e(devicePhoneNumber, "devicePhoneNumber");

                                if (currentUserPhoneNumber.equalsIgnoreCase("+" + devicePhoneNumber)) {
                                    authenticationStatusDisplay.setText("Authentication Status: SUCCEEDED");
                                    attendance.setAuthenticated(true);
                                    Toast.makeText(this, "SUCCEEDED", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show();
                                    authenticationStatusDisplay.setText("Authentication Status: FAILED");
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
                                attendance.setAttemptedAuthByUserMobileNumber(true);
                                repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                                //moveTaskToBack(true);
                            });
                });

    }

}
