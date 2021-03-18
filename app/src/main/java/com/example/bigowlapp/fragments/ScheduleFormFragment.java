package com.example.bigowlapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.Constants;
import com.example.bigowlapp.utils.GroupRecyclerViewListener;
import com.example.bigowlapp.utils.UserFragmentListener;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static com.example.bigowlapp.utils.DateTimeFormatter.dateFormatter;
import static com.example.bigowlapp.utils.DateTimeFormatter.timeFormatter;

public class ScheduleFormFragment extends Fragment
        implements DatePickerDialogFragment.DatePickedListener,
        TimePickerDialogFragment.TimePickedListener,
        GroupRecyclerViewListener,
        UserFragmentListener {

    private EditText editTitle;
    private Button groupButton;
    private Button selectUserButton;
    private LinearLayout usersListView;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;
    private Button editLocation;
    private Button confirmSetSchedule;

    private Calendar activeDateTime;
    private boolean isStartTime = false;

    private Calendar startDateTime;
    private Calendar endDateTime;

    private DialogFragment groupDialogFragment;

    private SetScheduleViewModel setScheduleViewModel;

    public static ScheduleFormFragment newInstance() {
        return new ScheduleFormFragment();
    }

    public ScheduleFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_form, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (setScheduleViewModel == null) {
            setScheduleViewModel = new ViewModelProvider(getActivity()).get(SetScheduleViewModel.class);
        }
        setupScheduleDataBind();
        subscribeToGroupData();
        subscribeToUserData();
    }

    private void setupScheduleDataBind() {
        setScheduleViewModel.getNewScheduleData().observe(this, schedule -> {
            editTitle.setText(schedule.getTitle());


            startDateTime = Calendar.getInstance();
            endDateTime = Calendar.getInstance();

            startDateTime.setTime(schedule.getStartTime().toDate());
            endDateTime.setTime(schedule.getEndTime().toDate());

            editStartDate.setText(dateFormatter(startDateTime));
            editStartTime.setText(timeFormatter(startDateTime));
            editEndDate.setText(dateFormatter(endDateTime));
            editEndTime.setText(timeFormatter(endDateTime));

            if (schedule.getStartTime().compareTo(schedule.getEndTime()) >= 0) {
                editStartDate.setTextColor(getResources().getColor(R.color.error, null));
                editStartTime.setTextColor(getResources().getColor(R.color.error, null));
            } else {
                editStartDate.setTextColor(Color.BLACK);
                editStartTime.setTextColor(Color.BLACK);
            }
        });

        setScheduleViewModel.getSelectedGroupData().observe(this, group -> {
            if (group == null) {
                return;
            }
            groupButton.setText(group.getName());
            groupButton.setError(null);
        });

        setScheduleViewModel.getSelectedLocationData().observe(this, location -> {
            if (location == null) {
                return;
            }
            String editLocationText = (location.placeName() == null) ? location.address() : location.placeName();
            editLocation.setText(editLocationText);
            editLocation.setError(null);
        });
    }

    private void initialize(View view) {
        editTitle = view.findViewById(R.id.edit_title_schedule);
        groupButton = view.findViewById(R.id.select_group_button);
        selectUserButton = view.findViewById(R.id.select_user_button);
        usersListView = view.findViewById(R.id.select_users_list_view);
        editStartDate = view.findViewById(R.id.edit_start_date);
        editStartTime = view.findViewById(R.id.edit_start_time);
        editEndDate = view.findViewById(R.id.edit_end_date);
        editEndTime = view.findViewById(R.id.edit_end_time);
        confirmSetSchedule = view.findViewById(R.id.set_schedule_confirm_button);
        editLocation = view.findViewById(R.id.edit_location);

        setupTitle();
        setupDateTimeButtons();
        setupSelectUserLayout();
        setupEditLocation();
        setupConfirmSetScheduleButton();
    }

    private void setupTitle() {
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needs to be checked before the title's change
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Nothing needs to be checked after the title's change
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setScheduleViewModel.updateScheduleTitle(s.toString());
            }
        });
    }

    private void subscribeToGroupData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfGroup().observe(this, groups ->
                groupDialogFragment = new GroupDialogFragment(this, groups));
        groupButton.setOnClickListener(view ->
                groupDialogFragment.show(getChildFragmentManager(), "groupDialogFragment"));
    }

    private void subscribeToUserData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfUserInGroupData()
                .observe(this, users -> {
                    if (setScheduleViewModel.getSelectedGroup() != null) {
                        selectUserButton.setEnabled(true);
                    }
                });
        setScheduleViewModel.getListOfSelectedUsersData().observe(this, selectedUsers -> {

            if (selectedUsers.isEmpty()) {
                selectUserButton.setVisibility(View.VISIBLE);
                usersListView.setVisibility(View.GONE);
            } else {
                selectUserButton.setVisibility(View.GONE);
                usersListView.setVisibility(View.VISIBLE);
                selectUserButton.setError(null);
            }

            List<String> userNamesArray =
                    selectedUsers.stream().map(User::getFullName).collect(Collectors.toList());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, userNamesArray);

            usersListView.setOnClickListener(view ->
                    getParentFragmentManager().beginTransaction()
                            .replace(((ViewGroup) getView().getParent()).getId(),
                                    new UserFragment(this,
                                            setScheduleViewModel
                                                    .getListOfUserInGroupData().getValue(),
                                            setScheduleViewModel
                                                    .getListOfSelectedUsersData().getValue()))
                            .addToBackStack(null)
                            .commit());

            addItemListToLinearLayout(adapter);
        });
    }

    private void addItemListToLinearLayout(ArrayAdapter<String> adapter) {
        final int count = adapter.getCount();
        usersListView.removeAllViews();
        for (int i = 0; i < count; i++) {
            View view = adapter.getView(i, null, usersListView);
            usersListView.addView(view);
        }
    }

    private void setupSelectUserLayout() {
        selectUserButton.setEnabled(false);
        selectUserButton.setOnClickListener(view ->
                getParentFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(),
                                new UserFragment(this,
                                        setScheduleViewModel.getListOfUserInGroupData().getValue(),
                                        setScheduleViewModel.getListOfSelectedUsersData().getValue()))
                        .addToBackStack(null)
                        .commit());
    }

    private void setupConfirmSetScheduleButton() {
        confirmSetSchedule.setOnClickListener(view -> {
            Schedule scheduleToAdd = setScheduleViewModel.getNewScheduleData().getValue();

            boolean hasError = false;

            if (scheduleToAdd == null) {
                hasError = true;
            }

            if (scheduleToAdd.getTitle().trim().isEmpty()) {
                editTitle.setError("Please enter title.");
                hasError = true;
            }

            if (scheduleToAdd.getGroupUid() == null) {
                groupButton.setError("Please select a group");
                hasError = true;
            }

            if (scheduleToAdd.getMemberList() == null || scheduleToAdd.getMemberList().isEmpty()) {
                selectUserButton.setError("Please select at least one user");
                hasError = true;
            }

            if (scheduleToAdd.getStartTime().compareTo(scheduleToAdd.getEndTime()) >= 0) {
                editStartDate.setTextColor(getResources().getColor(R.color.error, null));
                editStartTime.setTextColor(getResources().getColor(R.color.error, null));
                hasError = true;
            }

            if (scheduleToAdd.getLocation() == null) {
                editLocation.setError("Please select a location");
                hasError = true;
            }

            if (hasError) {
                Toast.makeText(getContext(), "Please complete all the necessary fields", Toast.LENGTH_LONG).show();
                return;
            }

            confirmationScreen(scheduleToAdd);
        });
    }

    private void setupDateTimeButtons() {
        editStartDate.setOnClickListener(view ->
                showDateDialogByButtonClick(startDateTime, true));

        editStartTime.setOnClickListener(view ->
                showTimeDialogByButtonClick(startDateTime, true));

        editEndDate.setOnClickListener(view ->
                showDateDialogByButtonClick(endDateTime, false));

        editEndTime.setOnClickListener(view ->
                showTimeDialogByButtonClick(endDateTime, false));
    }

    private void showDateDialogByButtonClick(Calendar dateTime, boolean isStart) {
        isStartTime = isStart;
        activeDateTime = dateTime;
        DialogFragment newFragment = DatePickerDialogFragment.newInstance(this);
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    private void showTimeDialogByButtonClick(Calendar dateTime, boolean isStart) {
        isStartTime = isStart;
        activeDateTime = dateTime;
        DialogFragment newFragment = TimePickerDialogFragment.newInstance(this);
        newFragment.show(getChildFragmentManager(), "timePicker");
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        activeDateTime.set(Calendar.YEAR, year);
        activeDateTime.set(Calendar.MONTH, month);
        activeDateTime.set(Calendar.DAY_OF_MONTH, day);
        if (isStartTime) {
            setScheduleViewModel.updateScheduleStartTime(startDateTime.getTime());
        } else {
            setScheduleViewModel.updateScheduleEndTime(endDateTime.getTime());
        }
    }

    @Override
    public void onTimePicked(int hour, int minute) {
        activeDateTime.set(Calendar.HOUR_OF_DAY, hour);
        activeDateTime.set(Calendar.MINUTE, minute);
        if (isStartTime) {
            setScheduleViewModel.updateScheduleStartTime(startDateTime.getTime());
        } else {
            setScheduleViewModel.updateScheduleEndTime(endDateTime.getTime());
        }
    }

    @Override
    public void onClickedGroup(Group group) {
        setScheduleViewModel.updateScheduleGroup(group);
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
                    .build(getActivity());

            startActivityForResult(locationIntent, Constants.REQUEST_CODE_LOCATION);
        });
    }

    private void handleLocationSelection(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            CarmenFeature location = PlaceAutocomplete.getPlace(data);
            setScheduleViewModel.updateScheduleLocation(location);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_CODE_LOCATION) {
            handleLocationSelection(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void userFragmentOnSubmitClicked(List<User> selectedUsers) {
        setScheduleViewModel.updateSelectedUsers(selectedUsers);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setSetScheduleViewModel(SetScheduleViewModel setScheduleViewModel) {
        this.setScheduleViewModel = setScheduleViewModel;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Button getGroupButton() {
        return groupButton;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Button getSelectUserButton() {
        return selectUserButton;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Button getEditLocation() {
        return editLocation;
    }

    public void confirmationScreen (Schedule schedule){
        Group group = setScheduleViewModel.getSelectedGroupData().getValue();
        List<User> userList = setScheduleViewModel.getListOfSelectedUsersData().getValue();
        CarmenFeature location = setScheduleViewModel.getSelectedLocationData().getValue();

        AlertDialog.Builder scheduleConfirmation = new AlertDialog.Builder(getContext());
        scheduleConfirmation.setTitle("New Schedule details");
        scheduleConfirmation.setMessage(confirmationString(group, userList, schedule, location));
        scheduleConfirmation.setPositiveButton("Confirm", (dialog, which) -> {
            setScheduleViewModel.addSchedule();
            Toast.makeText(getContext(), "Schedule added successfully", Toast.LENGTH_LONG).show();
            getActivity().finish();
        });

        scheduleConfirmation.setNegativeButton("Cancel", (dialog, which) -> {
        });
        scheduleConfirmation.show();
    }

    public String confirmationString (Group group, List<User> userList, Schedule schedule, CarmenFeature location){
        String title = schedule.getTitle();
        String groupName = group.getName();
        StringBuilder selectedMembers = new StringBuilder();

        for (User user : userList) {
            selectedMembers.append(user.getFullName()).append("\n");
        }

        String startDate = dateFormatter(startDateTime);
        String startTime = timeFormatter(startDateTime);
        String endDate = dateFormatter(endDateTime);
        String endTime = timeFormatter(endDateTime);

        String selectedLocation = (location.placeName() == null) ? location.address() : location.placeName();

        return ("Please confirm the new Schedule info: \n\nTitle:\n" + title + "\n\nGroup name:\n"+ groupName + "\n\nSelected members:\n"+ selectedMembers + "\nStart date:\n" + startDate + "\n"+ startTime + "\n\nEnd date\n" + endDate + "\n" + endTime + "\n\nSelected Location:\n" + selectedLocation);
    }

}