package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.DatePickerDialogFragment;
import com.example.bigowlapp.fragments.GroupDialogFragment;
import com.example.bigowlapp.fragments.TimePickerDialogFragment;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.GroupRecyclerViewListener;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class SetSchedule extends AppCompatActivity
        implements DatePickerDialogFragment.DatePickedListener,
        TimePickerDialogFragment.TimePickedListener,
        GroupRecyclerViewListener {

    private List<Group> listOfGroups;
    private List<User> listOfUsers;

    private Group currentGroup;

    private EditText editTitle;
    private Button groupButton;
    //private Spinner userSpinner;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;
    private Button confirmSetSchedule;

    private Button activeDateTimeButton;
    private Calendar activeDateTime;

    private DialogFragment dialogFragment;

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
        editTitle = findViewById(R.id.edit_title_schedule);
        groupButton = findViewById(R.id.select_group_button);
        //userSpinner = findViewById(R.id.select_user_spinner);
        editStartDate = findViewById(R.id.edit_start_date);
        editStartTime = findViewById(R.id.edit_start_time);
        editEndDate = findViewById(R.id.edit_end_date);
        editEndTime = findViewById(R.id.edit_end_time);
        confirmSetSchedule = findViewById(R.id.set_schedule_confirm_button);

        setupConfirmSetScheduleButton();
        setupDateTimeButtons();
        setupUsersInSpinner();


    }

    private void subscribeToGroupData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfGroup().observe(this, groups -> {
            //TODO: remove adapter
//            listOfGroups = new ArrayList<>(groups);
//            List<String> groupNamesArray =
//                    listOfGroups.stream().map(Group::getName).collect(Collectors.toList());
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                    android.R.layout.simple_spinner_item, groupNamesArray);
//
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            groupButton.setAdapter(adapter);
            dialogFragment = new GroupDialogFragment(groups);
        });
    }

    private void setupUsersInSpinner() {

        groupButton.setOnClickListener(view -> {
//            FragmentManager manager = getSupportFragmentManager();
//            FragmentTransaction transaction = manager.beginTransaction();
//            GroupFragment gf = new GroupFragment();
//            transaction.add(R.id.caca, gf);
//            transaction.addToBackStack(null);
//            transaction.commit();


            dialogFragment.show(getSupportFragmentManager(), "df");
        });

        //TODO: Remove list
//        groupButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("position: ", Integer.toString(position));
//                subscribeToUserData(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
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
                    //userSpinner.setAdapter(adapter);
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
            showDateDialogByButtonClick(startDateTime, editStartDate);
        });
        editStartTime.setOnClickListener(view -> {
            showTimeDialogByButtonClick(startDateTime, editStartTime);
        });
        editEndDate.setOnClickListener(view -> {
            showDateDialogByButtonClick(endDateTime, editEndDate);
        });
        editEndTime.setOnClickListener(view -> {
            showTimeDialogByButtonClick(endDateTime, editEndTime);
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
        return (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                calendar.get(Calendar.YEAR);
    }

    private String timeFormatter(Calendar calendar) {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":" +
                (minute < 10 ? ("0" + minute) : minute);
    }

    private void unregisterActiveDateTimeButton() {
        activeDateTimeButton = null;
    }

    private void showDateDialogByButtonClick(Calendar dateTime, Button buttonDisplay) {
        activeDateTime = dateTime;
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "startDatePicker");
    }

    private void showTimeDialogByButtonClick(Calendar dateTime, Button buttonDisplay) {
        activeDateTime = dateTime;
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "startTimePicker");
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        String strDate = (month + 1) + "/" + day + "/" + year;
        activeDateTime.set(Calendar.YEAR, year);
        activeDateTime.set(Calendar.MONTH, month);
        activeDateTime.set(Calendar.DAY_OF_MONTH, day);
        activeDateTimeButton.setText(strDate);
        unregisterActiveDateTimeButton();
    }

    @Override
    public void onTimePicked(int hour, int minute) {
        String strTime = ((hour < 10) ? "0" + hour : hour) + ":"
                + ((minute < 10) ? "0" + minute : minute);
        activeDateTime.set(Calendar.HOUR_OF_DAY, hour);
        activeDateTime.set(Calendar.MINUTE, minute);
        activeDateTimeButton.setText(strTime);
        unregisterActiveDateTimeButton();
    }

    @Override
    public void onClickedGroup(Group group) {
        currentGroup = group;
        groupButton.setText(currentGroup.getName());
    }
}