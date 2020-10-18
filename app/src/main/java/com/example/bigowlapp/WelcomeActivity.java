package com.example.bigowlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bigowlapp.ui.WelcomeFragment;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, WelcomeFragment.newInstance())
                    .commitNow();
        }
    }
}