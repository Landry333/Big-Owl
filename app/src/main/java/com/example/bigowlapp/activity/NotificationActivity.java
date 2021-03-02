package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.NotificationAdapter;
import com.example.bigowlapp.fragments.ScheduleFormFragment;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

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