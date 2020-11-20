package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.DatePickerDialogFragment;
import com.example.bigowlapp.fragments.TimePickerDialogFragment;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetSchedule extends AppCompatActivity
        implements DatePickerDialogFragment.DatePickedListener,
        TimePickerDialogFragment.TimePickedListener {

    private List<Group> listOfGroups;
    private List<User> listOfUsers;

    private Spinner groupSpinner;
    private Spinner userSpinner;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;

    private Button activeDateTimeButton;

    private String title;

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
        subscribeToGroupData();
    }

    private void initialize() {
        groupSpinner = (Spinner) findViewById(R.id.select_group_spinner);
        userSpinner = (Spinner) findViewById(R.id.select_user_spinner);
        editStartDate = (Button) findViewById(R.id.edit_start_date);
        editStartTime = (Button) findViewById(R.id.edit_start_time);
        editEndDate = (Button) findViewById(R.id.edit_end_date);
        editEndTime = (Button) findViewById(R.id.edit_end_time);
        setupDateTimeButtons();
        setUsersInSpinner();
    }

    private void subscribeToGroupData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfGroup().observe(this, groups -> {
            listOfGroups = new ArrayList<>(groups);
            List<String> groupNamesArray =
                    listOfGroups.stream().map(Group::getName).collect(Collectors.toList());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, groupNamesArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpinner.setAdapter(adapter);
        });
    }

    private void setUsersInSpinner() {
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("position: ", Integer.toString(position));
                subscribeToUserData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void subscribeToUserData(int position) {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfUsersFromGroup(listOfGroups.get(position))
                .observe(this, users -> {
                    listOfUsers = new ArrayList<>(users);

                    List<String> userNamesArray =
                            listOfUsers.stream().map(User::getFullName).collect(Collectors.toList());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, userNamesArray);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    userSpinner.setAdapter(adapter);
                });
    }

    private void setupDateTimeButtons() {
        editStartDate.setOnClickListener(view -> {
            showDateDialogByButtonClick(editStartDate);
        });
        editStartTime.setOnClickListener(view -> {
            showTimeDialogButtonClick(editStartTime);
        });
        editEndDate.setOnClickListener(view -> {
            showDateDialogByButtonClick(editEndDate);
        });
        editEndTime.setOnClickListener(view -> {
            showTimeDialogButtonClick(editEndTime);
        });
    }

    private void unregisterDateTimeButton() {
        activeDateTimeButton = null;
    }

    private void showDateDialogByButtonClick(Button buttonDisplay) {
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "startDatePicker");
    }

    private void showTimeDialogButtonClick(Button buttonDisplay) {
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "startTimePicker");
    }

    @Override
    public void onDatePicked(String strDate, int year, int month, int day) {
        activeDateTimeButton.setText(strDate);
        unregisterDateTimeButton();
    }

    @Override
    public void onTimePicked(String strTime, int hour, int minute) {
        activeDateTimeButton.setText(strTime);
        unregisterDateTimeButton();
    }

    private void sendSchedule() {

    }

}