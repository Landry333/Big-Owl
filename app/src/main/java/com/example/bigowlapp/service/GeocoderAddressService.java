package com.example.bigowlapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bigowlapp.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeocoderAddressService extends IntentService {

    public static final String ACTION_GET_ADDRESSES = "com.example.bigowlapp.action.GET_ADDRESSES";

    public static final String EXTRA_TEXT = "com.example.bigowlapp.extra.TEXT";
    public static final String EXTRA_ADDRESS_LIST_RESULT = "com.example.bigowlapp.extra.ADDRESS_LIST_RESULT";

    private static final int MAX_LOCATION_RESULTS = 5;

    private static String currentSearch = null;
    private static String nextToSearch = null;
    private int searchCount = 0;

    private ResultReceiver addressListResult;

    public GeocoderAddressService() {
        super("GeocoderAddressService");
    }

//    public static void startActionGetAddresses(Context context, String text) {
//        Intent intent = new Intent(context, GeocoderAddressService.class);
//        intent.setAction(ACTION_GET_ADDRESSES);
//        intent.putExtra(EXTRA_TEXT, text);
//        context.startService(intent);
//    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
//        if (intent != null) {
//            final String action = intent.getAction();
//
//            if (ACTION_GET_ADDRESSES.equals(action)) {
//                final String text = intent.getStringExtra(EXTRA_TEXT);
//
//                Log.d("Big Owl", "Will try to search: "  + text);
//                if (currentSearch == null && nextToSearch == null) {
//                    currentSearch = text;
//                    nextToSearch = text;
//                } else if (nextToSearch == null) {
//                    Log.d("Big Owl", "Still Searching, next will be: "  + text);
//                    nextToSearch = text;
//                } else {
//                    Log.d("Big Owl", "REPLACE, next to search is now"  + text);
//                }
//            }
//        }

        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_GET_ADDRESSES.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_TEXT);

                if (searchCount >= 2) {
                    Log.d("Big Owl", "TWO SEARCHES ALREADY QUEUED, SO REPLACE: " + nextToSearch + "--> Instead Search: " + text);
                    nextToSearch = text;
                    return START_NOT_STICKY;
                }

                if (searchCount == 1) {
                    Log.d("Big Owl", "A SEARCH IS RUNNING, NEXT WILL BE: " + text);
                    nextToSearch = text;
                }

                if (searchCount == 0) {
                    Log.d("Big Owl", "NO SEARCHES RUNNING, WILL SEARCH: " + text);
                    currentSearch = text;
                    nextToSearch = text;
                }
            }
        }

        ++searchCount;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_GET_ADDRESSES.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_TEXT);
                currentSearch = nextToSearch;
                Log.d("Big Owl", "NOW SEARCHING: "  + currentSearch);
                addressListResult = intent.getParcelableExtra(EXTRA_ADDRESS_LIST_RESULT);
                handleActionGetAddresses(currentSearch, addressListResult);
            }
        }
    }

    private void handleActionGetAddresses(String text, ResultReceiver addressListResult) {
        Bundle bundle = new Bundle();
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addressList = geocoder.getFromLocationName(text, MAX_LOCATION_RESULTS);

            if(addressList == null || addressList.isEmpty()) {
                bundle.putString(Constants.EXTRA_RESULT_DATA_KEY, "No Addresses Found");
                addressListResult.send(Constants.RESULT_CODE_FAIL, bundle);

                Log.d("Big Owl", "Finished searching: " + text + "\n IT DIDNT EXIST");
                --searchCount;
                Log.e("Big Owl", "Removing a search, now there is: " + searchCount);
                currentSearch = null;
                return;
            }

            bundle.putParcelableArrayList(Constants.EXTRA_RESULT_DATA_KEY, new ArrayList<>(addressList));
            addressListResult.send(Constants.RESULT_CODE_SUCCESS, bundle);
            Log.d("Big Owl", "Finished searching: " + text + "\n GOT " + addressList.size() + " addresses");
        } catch (IOException e) {
            Log.d("Big Owl", "Finished searching: " + text + "\n IT CAUSED ERROR");
            Log.e("Big Owl", Log.getStackTraceString(e));
        } finally {
            --searchCount;
            Log.d("Big Owl", "Removing a search, now there is: " + searchCount);
            currentSearch = null;
        }
    }


}