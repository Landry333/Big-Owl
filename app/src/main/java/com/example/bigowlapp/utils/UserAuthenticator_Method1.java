package com.example.bigowlapp.utils;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.viewModel.HomePageViewModel;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class UserAuthenticator_Method1 extends AppCompatActivity {

    private String deviceID="myID 789";
    private String devicePhoneNumber;
    private String currentUserPhoneNumber;
    private boolean authResult = false;

    private final AuthRepository authRepository = new AuthRepository();


    UserAuthenticator_Method1() {
        // This is a util class, it has no state
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserPhoneNumber = authRepository.getCurrentUser().getPhoneNumber();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 10);
        }
        else onStart();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //deviceID = telephonyManager.getDeviceId();
                //deviceID ="deviceID";
                Toast.makeText(this, "Phone State Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone State Permission DENIED", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceID = telephonyManager.getDeviceId();
        devicePhoneNumber = telephonyManager.getLine1Number();
        if(devicePhoneNumber == currentUserPhoneNumber){
            authResult=true;
            return;
        }
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDeviceID() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getImei();
    }

    public boolean isAuthResult() {
        return authResult;
    }
}