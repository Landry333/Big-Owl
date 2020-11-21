package com.example.bigowlapp.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class SetSchedule extends AppCompatActivity {

    private static final int MAX_LOCATION_RESULTS = 5;
    private static final int MIN_CHARS_FOR_ADDRESS_SEARCH = 3;

    private List<Group> listOfGroups;
    private List<User> listOfUsers;

    private Spinner groupSpinner;
    private Spinner userSpinner;

    private Geocoder geocoder;
    private ArrayAdapter<Address> addressArrayAdapter;
    private List<Address> addressList;
    private AutoCompleteTextView editLocation;

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

        setUsersInSpinner();
        setupEditLocation();
    }

    private void setupEditLocation() {
        geocoder = new Geocoder(this, Locale.getDefault());

        // TODO: remove test code
        ArrayList<String> testAddressStringList = new ArrayList<>();
        testAddressStringList.add("Apple");
        testAddressStringList.add("Appliance");
        testAddressStringList.add("AppleWatcher");
        testAddressStringList.add("coowabara");

        addressList = new ArrayList<>();
        addressArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addressList);

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

                getAddresses(text).thenAcceptAsync(addresses -> {
                    Log.d("BigOwl", "pizzapizza"); // TODO: remove
                    if (addresses.isEmpty()) {
                        return;
                    }

                    Log.d("BigOwl", addresses.size() + " \n " + addresses.get(0).toString()); // TODO: remove
                    addressArrayAdapter.clear();
                    addressArrayAdapter.addAll(addresses);
                    addressArrayAdapter.notifyDataSetChanged();
                    editLocation.showDropDown();
                }).exceptionally(e -> {

                    Log.d("BigOwl", "pizza_FAIL"); // TODO: remove
                    Log.e("BigOwl", Log.getStackTraceString(e));
                    return null;
                });
            }

        });
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

    private void sendSchedule() {

    }
}