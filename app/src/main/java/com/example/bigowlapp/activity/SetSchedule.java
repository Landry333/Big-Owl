package com.example.bigowlapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.DatePickerDialogFragment;
import com.example.bigowlapp.fragments.GroupDialogFragment;
import com.example.bigowlapp.fragments.TimePickerDialogFragment;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.Constants;
import com.example.bigowlapp.utils.GroupRecyclerViewListener;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

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

    private EditText editTitle;
    private Button groupButton;
    private ListView usersListView;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;
    private Button confirmSetSchedule;

    private LinearLayout selectUserLayout;

    private Button activeDateTimeButton;
    private Calendar activeDateTime;

    private DialogFragment groupDialogFragment;

    private Calendar startDateTime;
    private Calendar endDateTime;

    private Button editLocation;
    private GeoPoint selectedLocationLatLng;

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
        editTitle = findViewById(R.id.edit_title_schedule);
        groupButton = findViewById(R.id.select_group_button);
        usersListView = findViewById(R.id.select_users_list_view);
        selectUserLayout = findViewById(R.id.select_users_container);
        editStartDate = findViewById(R.id.edit_start_date);
        editStartTime = findViewById(R.id.edit_start_time);
        editEndDate = findViewById(R.id.edit_end_date);
        editEndTime = findViewById(R.id.edit_end_time);
        confirmSetSchedule = findViewById(R.id.set_schedule_confirm_button);

        setupDateTimeButtons();
        setupSelectUserLayout();
        setupEditLocation();
        setupConfirmSetScheduleButton();
    }

    private void subscribeToGroupData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfGroup().observe(this, groups -> {
            groupDialogFragment = new GroupDialogFragment(groups);
        });

        groupButton.setOnClickListener(view -> {
            groupDialogFragment.show(getSupportFragmentManager(), "groupDialogFragment");
        });
    }

    private void setupUsersInSpinner() {
        // TODO: Used in OnClickedGroup(), specify the list of users
    }

    private void subscribeToUserData(Group group) {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfUsersFromGroup(group)
                .observe(this, users -> {
                    listOfUsers = new ArrayList<>(users);

                    List<String> userNamesArray =
                            listOfUsers.stream().map(User::getFullName).collect(Collectors.toList());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1, userNamesArray);

                    usersListView.setOnItemClickListener((parent, view, position, id) -> {
                        Intent i = new Intent(SetSchedule.this, SelectUsersInGroupActivity.class);
                        startActivity(i);
                    });

                    usersListView.setAdapter(adapter);

                    //TODO: Optimize or change code to be more clean when linearlayout is implemented
                    int height = 0;
                    for(int i = 0; i < adapter.getCount(); i++){
                        View viewItem = adapter.getView(i, null, usersListView);
                        viewItem.measure(0, 0);
                        height += viewItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = usersListView.getLayoutParams();
                    params.height = height + (usersListView.getDividerHeight()
                            * (adapter.getCount() - 1));
                    usersListView.setLayoutParams(params);
                    usersListView.setAnimation(null);
                    usersListView.requestLayout();
                });
    }

    private void setupSelectUserLayout() {
        selectUserLayout.setEnabled(false);
        selectUserLayout.setOnClickListener(view ->{
            Intent i = new Intent(SetSchedule.this, SelectUsersInGroupActivity.class);
            startActivity(i);
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
        subscribeToUserData(group);
        selectUserLayout.setEnabled(true);
        setScheduleViewModel.setCurrentGroup(group);
        groupButton.setText(group.getName());
    }

    private void setupEditLocation() {
        editLocation.setOnClickListener(v -> {
            PlaceOptions placeOptions =
                    PlaceOptions.builder()
                            .backgroundColor(Color.WHITE)
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