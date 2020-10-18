package com.example.bigowlapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private NotificationRepository notificationRepository;
    private LiveData<List<Notification>> notificationListData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerView = findViewById(R.id.recyclerview_notifications);

        notificationRepository = new NotificationRepository();
        notificationListData = notificationRepository.getAllDocumentsFromCollection(Notification.class);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        notificationListData.observe(this, notifications -> {
            ArrayList<String> type = new ArrayList<>();
            for(Notification n : notifications){
                type.add(n.getType());
            }

            mAdapter = new NotificationAdapter(type , this);
        });

        recyclerView.setAdapter(mAdapter);

    }
}