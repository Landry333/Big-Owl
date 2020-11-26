package com.example.bigowlapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.ScheduleFormFragment;

public class SetScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.schedule_form_container, ScheduleFormFragment.newInstance())
                    .commitNow();
        }
    }
}