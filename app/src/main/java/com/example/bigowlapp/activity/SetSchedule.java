package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.DatePickerDialogFragment;
import com.example.bigowlapp.fragments.TimePickerDialogFragment;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.Constants;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

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

    private Button editLocation;
    private GeoPoint selectedLocationLatLng;

    private String title;

    private SetScheduleViewModel setScheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);

        editLocation = findViewById(R.id.edit_location);

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
        groupSpinner = findViewById(R.id.select_group_spinner);
        userSpinner = findViewById(R.id.select_user_spinner);
        editStartDate = findViewById(R.id.edit_start_date);
        editStartTime = findViewById(R.id.edit_start_time);
        editEndDate = findViewById(R.id.edit_end_date);
        editEndTime = findViewById(R.id.edit_end_time);

        setupDateTimeButtons();
        setUsersInSpinner();
        setupEditLocation();
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

    private void setupEditLocation() {
        editLocation.setOnClickListener(v -> {
            PlaceOptions placeOptions = PlaceOptions.builder()
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS);

            Intent locationIntent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(getResources().getString(R.string.mapbox_public_access_token))
                    .placeOptions(placeOptions)
                    .build(this);

            startActivityForResult(locationIntent, Constants.REQUEST_CODE_LOCATION);
        });
    }

    private void handleLocationSelection(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            CarmenFeature location = PlaceAutocomplete.getPlace(data);
            String editLocationText = (location.placeName() == null) ? location.address() : location.placeName();
            editLocation.setText(editLocationText);
            selectedLocationLatLng = new GeoPoint(location.center().latitude(), location.center().longitude());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_CODE_LOCATION) {
            handleLocationSelection(resultCode, data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}