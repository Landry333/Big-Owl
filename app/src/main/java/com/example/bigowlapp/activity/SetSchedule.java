package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetSchedule extends AppCompatActivity {

    private SetScheduleViewModel setScheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (setScheduleViewModel == null) {
            setScheduleViewModel = new ViewModelProvider(this).get(SetScheduleViewModel.class);
        }
        subscribeToData();
    }

    private void initialize() {

    }

    private void subscribeToData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }

        Spinner groupSpinner = (Spinner) findViewById(R.id.select_group_spinner);
        setScheduleViewModel.getListOfGroup().observe(this, groups -> {
            List<Group> groupSpinnerArray = new ArrayList<>(groups);
            List<String> groupNamesArray =
                    groupSpinnerArray.stream().map(Group::getName).collect(Collectors.toList());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, groupNamesArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpinner.setAdapter(adapter);
            groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("position: ", Integer.toString(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        });
    }
}