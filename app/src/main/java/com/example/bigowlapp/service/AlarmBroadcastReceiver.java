package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Run the location/Geofencing code
        // Intent should have
        /*
          intent.putExtra("Uid", schedule.getUid());
          intent.putExtra("Latitude", schedule.getLocation().getLatitude());
          intent.putExtra("Longitude", schedule.getLocation().getLongitude());
         */
    }
}
