package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class SetSchedule extends AppCompatActivity
        implements DatePickerDialogFragment.DatePickedListener,
        TimePickerDialogFragment.TimePickedListener {

    private List<Group> listOfGroups;
    private List<User> listOfUsers;

    private EditText editTitle;
    private Spinner groupSpinner;
    private Spinner userSpinner;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;
    private Button confirmSetSchedule;

    private Button activeDateTimeButton;

    private Calendar startDateTime;
    private Calendar endDateTime;

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
        editTitle = (EditText) findViewById(R.id.edit_title_schedule);
        groupSpinner = (Spinner) findViewById(R.id.select_group_spinner);
        userSpinner = (Spinner) findViewById(R.id.select_user_spinner);
        editStartDate = (Button) findViewById(R.id.edit_start_date);
        editStartTime = (Button) findViewById(R.id.edit_start_time);
        editEndDate = (Button) findViewById(R.id.edit_end_date);
        editEndTime = (Button) findViewById(R.id.edit_end_time);
        confirmSetSchedule = (Button) findViewById(R.id.set_schedule_confirm_button);

        setupConfirmSetScheduleButton();
        setupDateTimeButtons();
        setupUsersInSpinner();
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

    private void setupUsersInSpinner() {
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

    private void setupConfirmSetScheduleButton() {
        confirmSetSchedule.setOnClickListener(view -> {
            // TODO: verify that all information is valid
        });
    }

    //===========================================================================================
    // Time & Date
    //===========================================================================================

    private void setupDateTimeButtons() {
        editStartDate.setOnClickListener(view -> {
            showDateDialogByButtonClick(editStartDate);
        });
        editStartTime.setOnClickListener(view -> {
            showTimeDialogByButtonClick(editStartTime);
        });
        editEndDate.setOnClickListener(view -> {
            showDateDialogByButtonClick(editEndDate);
        });
        editEndTime.setOnClickListener(view -> {
            showTimeDialogByButtonClick(editEndTime);
        });

        startDateTime = Calendar.getInstance();
        endDateTime = Calendar.getInstance();
        endDateTime.add(Calendar.HOUR_OF_DAY, 1);

        editStartDate.setText(dateFormatter(startDateTime));
        editStartTime.setText(timeFormatter(startDateTime));
        editEndDate.setText(dateFormatter(endDateTime));
        editEndTime.setText(timeFormatter(endDateTime));
    }

    private String dateFormatter(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.YEAR);
    }

    private String timeFormatter(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE);
    }

    private void unregisterActiveDateTimeButton() {
        activeDateTimeButton = null;
    }

    private void showDateDialogByButtonClick(Button buttonDisplay) {
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "startDatePicker");
    }

    private void showTimeDialogByButtonClick(Button buttonDisplay) {
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "startTimePicker");
    }

    @Override
    public void onDatePicked(String strDate, int year, int month, int day) {
        activeDateTimeButton.setText(strDate);
        unregisterActiveDateTimeButton();
    }

    @Override
    public void onTimePicked(String strTime, int hour, int minute) {
        activeDateTimeButton.setText(strTime);
        unregisterActiveDateTimeButton();
    }

}