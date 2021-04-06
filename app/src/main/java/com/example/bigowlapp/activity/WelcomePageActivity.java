package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.Constants;
import com.uxcam.UXCam;

public class WelcomePageActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        UXCam.startWithKey("u5kc5hd96a2m2vm");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(WelcomePageActivity.this, LoginPageActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_DURATION);
    }
}