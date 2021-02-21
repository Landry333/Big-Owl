package com.example.bigowlapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DeviceIDNumberGetter extends AppCompatActivity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String IDNumber = telephonyManager.getImei();

    public DeviceIDNumberGetter() {
        // This is a util class, it has no state
    }


    /*@SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceIDNumber = telephonyManager.getDeviceId();
        return;
    }*/


    public String getIDNumber() {
        return IDNumber;
    }

}

