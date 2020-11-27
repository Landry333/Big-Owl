package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.ScheduleResponseViewModel;
import com.example.bigowlapp.viewModel.SupervisedGroupListViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ListOfScheduleActivity extends AppCompatActivity {
    private ListView scheduleListView;
    private ScheduleResponseViewModel scheduleListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_schedule);
        initialize();
    }

    protected void onStart() {
        super.onStart();
        if (scheduleListViewModel == null) {
            scheduleListViewModel = new ViewModelProvider(this).get(ScheduleResponseViewModel.class);
        }
        //subscribeToData();
    }

    private void initialize() {




    }
}