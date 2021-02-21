package com.example.bigowlapp.activity;

import androidx.annotation.NonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.AuthAttempted1Failure;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;

public class AuthenticationActivity extends BigOwlActivity {

    private String deviceIdNumber;
    private String devicePhoneNumber;
    private TextView deviceIdDisplay;
    private TextView authenticationStatusDisplay;
    private String currentUserPhoneNumber;
    private boolean didAttend = false;

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
        return R.layout.activity_authentication;
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
    //getContentView

    public String getDeviceIdNumber() {
        return deviceIdNumber;
    }

    public void setAuthenticated(){

    }

    @SuppressLint("MissingPermission")
    protected void onStart() {
        super.onStart();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        devicePhoneNumber = telephonyManager.getLine1Number();
        deviceIdDisplay = findViewById(R.id.device_Id_Display);
        deviceIdDisplay.setText("Device ID Number: "+ getDeviceIdNumber());

        authenticationStatusDisplay= findViewById(R.id.authentication_Status_Display);
        currentUserPhoneNumber = authRepository.getCurrentUser().getPhoneNumber();
        repositoryFacade.getScheduleRepository().getDocumentByUid("docId", Schedule.class)
                .observe(this,schedule -> {
                    UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                            .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                    Attendance attendance = userScheduleResponse.getAttendance();
                    boolean didAttend = attendance.didAttend();
                    boolean authAttempted_Method1 = attendance.isAuthAttempted_Method1();
                    if(!didAttend){
                        if(!currentUserPhoneNumber.equalsIgnoreCase(devicePhoneNumber) && !authAttempted_Method1 ){
                            authenticationStatusDisplay.setText("Authentication Status: FAILED");
                            attendance.setDeviceIdNumber(deviceIdNumber);
                            attendance.setAuthAttempted_Method1(true);
                            repositoryFacade.getScheduleRepository().addDocument(schedule);
                            AuthAttempted1Failure authAttempted1Failure = new AuthAttempted1Failure();
                        }
                        else{
                            authenticationStatusDisplay.setText("Authentication Status: SUCCEEDED");
                        }
                    }
                });

    }

    public boolean isDidAttend() {
        return didAttend;
    }


}

