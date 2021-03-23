package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.utils.MemberScheduleAlarmManager;
import com.example.bigowlapp.utils.ScheduledLocationTrackingManager;
import com.google.firebase.firestore.GeoPoint;

import static com.example.bigowlapp.utils.IntentConstants.*;

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

        ScheduledLocationTrackingManager locationTrackingManager = new ScheduledLocationTrackingManager(context);
        locationTrackingManager.addNewScheduledLocationToTrack(activatedSchedule);
    }

    private Schedule getSchedule(Intent intent) {
        Schedule schedule = new Schedule();
        Bundle bundle = intent.getExtras();
        schedule.setUid(bundle.getString(EXTRA_UID));
        schedule.setTitle(bundle.getString(EXTRA_SCHEDULE_TITLE));
        GeoPoint geoPoint = new GeoPoint(bundle.getDouble(EXTRA_LATITUDE),
                bundle.getDouble(EXTRA_LONGITUDE));
        schedule.setLocation(geoPoint);
        return schedule;
    }

}
