package com.example.bigowlapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.bigowlapp.adapter.NotificationAdapter;
import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private NotificationRepository notificationRepository;
    private LiveData<List<Notification>> notificationListData;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerView = findViewById(R.id.recyclerview_notifications);

        authRepository = new AuthRepository();
        notificationRepository = new NotificationRepository();
        notificationListData = notificationRepository.getListOfDocumentByAttribute("receiverUId", authRepository.getCurrentUser().getUid(), Notification.class);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        notificationListData.observe(this, notifications -> {
            if(notifications == null) {
                notifications = new ArrayList<>();
            }
            mAdapter = new NotificationAdapter(notifications , this);
            recyclerView.setAdapter(mAdapter);
        });
    }
}