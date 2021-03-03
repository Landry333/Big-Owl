package com.example.bigowlapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;

import java.util.Calendar;

public class AuthenticationActivityMethod2 extends BigOwlActivity {

    private String deviceIdNumber;
    private TextView deviceIdDisplay;
    private TextView authenticationStatusDisplay;
    private String scheduleId = getIntent().getStringExtra("scheduleId");

    private final AuthRepository authRepository = new AuthRepository();
    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moveTaskToBack(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 10);
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_authentication_method1;
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

    @SuppressLint("MissingPermission")
    protected void onStart() {
        super.onStart();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        deviceIdDisplay = findViewById(R.id.device_Id_Display_2);
        deviceIdDisplay.setText("Device ID Number: " + getDeviceIdNumber());

        authenticationStatusDisplay = findViewById(R.id.authentication_Status_Display_2);
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observe(this, schedule -> {
                    UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                            .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                    Attendance attendance = userScheduleResponse.getAttendance();
                    String savedDeviceId = attendance.getDeviceIdNumber();
                    Attendance.LocatedStatus scheduleLocated = attendance.getScheduleLocated();

                    if (scheduleLocated == Attendance.LocatedStatus.CORRECT_LOCATION && deviceIdNumber.equalsIgnoreCase(savedDeviceId)) {
                        authenticationStatusDisplay.setText("Authentication Status: SUCCEEDED");
                        attendance.setAuthenticated(true);
                    } else {
                        authenticationStatusDisplay.setText("Authentication Status: FAILED");
                    }
                    attendance.setAuthAttempted_Method2(true);
                    Timestamp currentTime = new Timestamp(Calendar.getInstance().getTime());
                    attendance.setAuthenticationTime(currentTime);
                    repositoryFacade.getScheduleRepository().addDocument(schedule);
                });

    }

}

