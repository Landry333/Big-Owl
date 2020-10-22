package com.example.bigowlapp.activityPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.Constants;

public class WelcomeActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(WelcomeActivity.this, LoginPageActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_DURATION);
    }
}