package com.example.bigowlapp.activity;

import android.os.Bundle;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.NotificationListFragment;

public class NotificationActivity extends BigOwlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.notification_fragment_container, NotificationListFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_notification;
    }
}