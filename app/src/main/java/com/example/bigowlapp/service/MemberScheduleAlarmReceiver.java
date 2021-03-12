package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.utils.MemberScheduleAlarmManager;
import com.example.bigowlapp.utils.ScheduledLocationTrackingManager;
import com.google.firebase.firestore.GeoPoint;

import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LATITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_LONGITUDE;
import static com.example.bigowlapp.utils.IntentConstants.EXTRA_UID;

/**
 * The purpose of this BroadcastReceiver is to execute code after the time activation of an alarm.
 * This class is specifically designed towards member's schedules.
 * The alarms are set/defined in
 * {@link MemberScheduleAlarmManager}
 */
public class MemberScheduleAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Schedule activatedSchedule = getSchedule(intent);
        Toast.makeText(context, "Received alarm and started location tracking", Toast.LENGTH_LONG).show();
        ScheduledLocationTrackingManager locationTrackingManager = new ScheduledLocationTrackingManager(context);
        locationTrackingManager.addNewScheduledLocationToTrack(activatedSchedule)
                .addOnSuccessListener(var -> {Toast.makeText(context, " tracking started", Toast.LENGTH_LONG).show();})
                .addOnFailureListener(var -> {Toast.makeText(context, " tracking FAILED", Toast.LENGTH_LONG).show();
                Toast.makeText(context, var.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Tracking Error", var.getMessage());
                });

    }

    private Schedule getSchedule(Intent intent) {
        double defaultValueLatLng = 0.0;
        Schedule schedule = new Schedule();
        schedule.setUid(intent.getStringExtra(EXTRA_UID));
        GeoPoint geoPoint = new GeoPoint(intent.getDoubleExtra(EXTRA_LATITUDE, defaultValueLatLng),
                intent.getDoubleExtra(EXTRA_LONGITUDE, defaultValueLatLng));
        schedule.setLocation(geoPoint);
        return schedule;
    }

}
