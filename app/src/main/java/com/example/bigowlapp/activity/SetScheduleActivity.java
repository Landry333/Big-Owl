package com.example.bigowlapp.activity;

import android.os.Bundle;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.ScheduleFormFragment;

public class SetScheduleActivity extends BigOwlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.schedule_form_container, ScheduleFormFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_set_schedule;
    }
}