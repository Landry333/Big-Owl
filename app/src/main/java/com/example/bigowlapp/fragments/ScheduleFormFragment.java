package com.example.bigowlapp.fragments;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.Constants;
import com.example.bigowlapp.utils.GroupRecyclerViewListener;
import com.example.bigowlapp.utils.UserFragmentListener;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

public class ScheduleFormFragment extends Fragment
        implements DatePickerDialogFragment.DatePickedListener,
        TimePickerDialogFragment.TimePickedListener,
        GroupRecyclerViewListener,
        UserFragmentListener {

    private EditText editTitle;
    private Button groupButton;
    private ListView usersListView;
    private LinearLayout selectUserLayout;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;
    private Button editLocation;
    private Button confirmSetSchedule;

    private Button activeDateTimeButton;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        });

        setScheduleViewModel.getSelectedGroupData().observe(this, group -> {
            if (group == null) {
                return;
            }
            groupButton.setText(group.getName());
        });

        setScheduleViewModel.getSelectedLocationData().observe(this, location -> {
            if (location == null) {
                return;
            }
            String editLocationText = (location.placeName() == null) ? location.address() : location.placeName();
            editLocation.setText(editLocationText);
        });
    }

    private void initialize(View view) {
        editTitle = view.findViewById(R.id.edit_title_schedule);
        groupButton = view.findViewById(R.id.select_group_button);
        usersListView = view.findViewById(R.id.select_users_list_view);
        selectUserLayout = view.findViewById(R.id.select_users_container);
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
        setScheduleViewModel.getListOfGroup().observe(this, groups -> {
            groupDialogFragment = new GroupDialogFragment(this, groups);
        });
        groupButton.setOnClickListener(view -> {
            groupDialogFragment.show(getChildFragmentManager(), "groupDialogFragment");
        });
    }

    private void subscribeToUserData() {
        if (!setScheduleViewModel.isCurrentUserSet()) {
            return;
        }
        setScheduleViewModel.getListOfUserInGroupData()
                .observe(this, users -> {
                    if (setScheduleViewModel.getSelectedGroup() != null) {
                        selectUserLayout.setEnabled(true);
                    }
                });
        setScheduleViewModel.getListOfSelectedUsersData().observe(this, selectedUsers -> {

                    List<String> userNamesArray =
                            selectedUsers.stream().map(User::getFullName).collect(Collectors.toList());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, userNamesArray);

                    usersListView.setOnItemClickListener((parent, view, position, id) -> {
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.schedule_form_container,
                                        new UserFragment(this,
                                                setScheduleViewModel
                                                        .getListOfUserInGroupData().getValue(),
                                                setScheduleViewModel
                                                        .getListOfSelectedUsersData().getValue()))
                                .addToBackStack(null)
                                .commit();
                    });

                    usersListView.setAdapter(adapter);

            //TODO: Optimize or change code to be more clean when linearlayout is implemented
                    int height = 0;
                    for (int i = 0; i < adapter.getCount(); i++) {
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
        selectUserLayout.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.schedule_form_container,
                            new UserFragment(this,
                                    setScheduleViewModel.getListOfUserInGroupData().getValue(),
                                    setScheduleViewModel.getListOfSelectedUsersData().getValue()))
                    .addToBackStack(null)
                    .commit();
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
            showDateDialogByButtonClick(startDateTime, editStartDate, true);
        });
        editStartTime.setOnClickListener(view -> {
            showTimeDialogByButtonClick(startDateTime, editStartTime, true);
        });
        editEndDate.setOnClickListener(view -> {
            showDateDialogByButtonClick(endDateTime, editEndDate, false);
        });
        editEndTime.setOnClickListener(view -> {
            showTimeDialogByButtonClick(endDateTime, editEndTime, false);
        });
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

    private void showDateDialogByButtonClick(Calendar dateTime, Button buttonDisplay, boolean isStart) {
        isStartTime = isStart;
        activeDateTime = dateTime;
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = DatePickerDialogFragment.newInstance(this);
        newFragment.show(getChildFragmentManager(), "startDatePicker");
    }

    private void showTimeDialogByButtonClick(Calendar dateTime, Button buttonDisplay, boolean isStart) {
        isStartTime = isStart;
        activeDateTime = dateTime;
        activeDateTimeButton = buttonDisplay;
        DialogFragment newFragment = TimePickerDialogFragment.newInstance(this);
        newFragment.show(getChildFragmentManager(), "startTimePicker");
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
        unregisterActiveDateTimeButton();
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
        unregisterActiveDateTimeButton();
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
        switch (requestCode) {
            case Constants.REQUEST_CODE_LOCATION:
                handleLocationSelection(resultCode, data);
                break;
            case Constants.REQUEST_CODE_USER_SELECTION:
                // TODO: handle the data that has been given (Unnecessary anymore)
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void userFragmentOnSubmitClicked(List<User> selectedUsers) {
        setScheduleViewModel.updateSelectedUsers(selectedUsers);
    }
}