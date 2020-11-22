package com.example.bigowlapp.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.adapter.AddressArrayAdapter;
import com.example.bigowlapp.fragments.DatePickerDialogFragment;
import com.example.bigowlapp.fragments.TimePickerDialogFragment;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.service.GeocoderAddressService;
import com.example.bigowlapp.utils.Constants;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class SetSchedule extends AppCompatActivity
        implements DatePickerDialogFragment.DatePickedListener,
        TimePickerDialogFragment.TimePickedListener {

    private static final int MAX_LOCATION_RESULTS = 5;
    private static final int MIN_CHARS_FOR_ADDRESS_SEARCH = 3;

    private List<Group> listOfGroups;
    private List<User> listOfUsers;

    private Spinner groupSpinner;
    private Spinner userSpinner;
    private Button editStartDate;
    private Button editStartTime;
    private Button editEndDate;
    private Button editEndTime;

    private Button activeDateTimeButton;

    private Geocoder geocoder;
    private AddressArrayAdapter addressArrayAdapter;
    private List<Address> addressList;
    private AutoCompleteTextView editLocation;
    private AddressResultReciever addressListResultReceiver;

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

    private void setupEditLocation() {
        addressListResultReceiver = new AddressResultReciever(null);
        geocoder = new Geocoder(this, Locale.getDefault());

        // TODO: remove test code
        ArrayList<String> testAddressStringList = new ArrayList<>();
        testAddressStringList.add("Apple");
        testAddressStringList.add("Appliance");
        testAddressStringList.add("AppleWatcher");
        testAddressStringList.add("coowabara");

        addressList = new ArrayList<>();
        addressArrayAdapter = new AddressArrayAdapter(this, R.layout.list_item_address, addressList);

        editLocation.setThreshold(0);
        editLocation.setAdapter(addressArrayAdapter);

        editLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needed before text change
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing needed after text change
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                if (text.length() < MIN_CHARS_FOR_ADDRESS_SEARCH) {
                    return;
                }

                startServiceToGetAddressList(text);
//                getAddresses(text).thenAcceptAsync(addresses -> {
//                    Log.d("BigOwl", "pizzapizza"); // TODO: remove
//                    if (addresses.isEmpty()) {
//                        Log.d("BigOwl", "no addresses"); // TODO: remove
//                        return;
//                    }
//
//                    Log.d("BigOwl", addresses.size() + " \n " + addresses.get(0).toString()); // TODO: remove
//                    addressArrayAdapter.clear();
//                    addressArrayAdapter.addAll(addresses);
//                    addressArrayAdapter.notifyDataSetChanged();
//                }).exceptionally(e -> {
//                    Log.d("BigOwl", "pizza_FAIL"); // TODO: remove
//                    Log.e("BigOwl", Log.getStackTraceString(e));
//                    return null;
//                });
            }

        });
    }

    private void startServiceToGetAddressList(String text) {
        Intent intent = new Intent(this, GeocoderAddressService.class);
        intent.setAction(GeocoderAddressService.ACTION_GET_ADDRESSES);
        intent.putExtra(GeocoderAddressService.EXTRA_ADDRESS_LIST_RESULT, addressListResultReceiver);
        intent.putExtra(GeocoderAddressService.EXTRA_TEXT, text);
        startService(intent);
    }

    private CompletableFuture<List<Address>> getAddresses(String searchText) throws CompletionException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return geocoder.getFromLocationName(searchText, MAX_LOCATION_RESULTS);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
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

    class AddressResultReciever extends ResultReceiver {
        public AddressResultReciever(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case Constants.RESULT_CODE_SUCCESS:
                    runOnUiThread(() -> {
                        List<Address> addressesFromGeocoderSearch = resultData.getParcelableArrayList(Constants.EXTRA_RESULT_DATA_KEY);
                        addressArrayAdapter.clear();
                        addressArrayAdapter.addAll(addressesFromGeocoderSearch);
                        addressArrayAdapter.notifyDataSetChanged();
                    });

                    break;
                case Constants.RESULT_CODE_FAIL:
                    String errorMessage = resultData.getString(Constants.EXTRA_RESULT_DATA_KEY);
                    Log.d("BigOwl", errorMessage); // TODO: remove
                    break;
            }
        }
    }

}