package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The purpose of this BroadcastReceiver is to execute code after the time activation of an alarm.
 * The alarms are set/defined in
 * {@link com.example.bigowlapp.utils.AlarmBroadcastReceiverManager}
 */
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
