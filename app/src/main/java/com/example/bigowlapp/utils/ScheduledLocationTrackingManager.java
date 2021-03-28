package com.example.bigowlapp.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.service.LocationBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.GeoPoint;

public class ScheduledLocationTrackingManager {

    private static final int REQUEST_CODE = 2;
    private static final int TRACKING_RADIUS_METERS = 300;
    private static final long TRACKING_EXPIRE_TIME_MILLIS = Schedule.MAX_TRACKING_TIME_MILLIS;
    // use 0 for instant response, and allow longer delays for better battery life
    private static final int MAX_NOTIFY_DELAY_MILLIS = 3 * Constants.MINUTE_TO_MILLIS;

    private final Context context;
    private final GeofencingClient geofencingClient;
    private final PeriodicLocationCheckAlarmManager locationCheckAlarmManager;
    private final LocationTrackingExpiredAlarmManager locationTrackingExpiredAlarmManager;
    private PendingIntent geofencePendingIntent;

    public ScheduledLocationTrackingManager(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
        locationCheckAlarmManager = new PeriodicLocationCheckAlarmManager(context);
        locationTrackingExpiredAlarmManager = new LocationTrackingExpiredAlarmManager(context);
    }

    public Task<Void> addNewScheduledLocationToTrack(Schedule scheduleWithLocationToTrack) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Tasks.forException(new SecurityException("Requires " + Manifest.permission.ACCESS_FINE_LOCATION + " permission."));
        }

        return geofencingClient
                .addGeofences(buildRequestToTrack(scheduleWithLocationToTrack), getGeofencePendingIntent())
                .onSuccessTask(aVoid -> {
                    locationCheckAlarmManager.setAlarm(MAX_NOTIFY_DELAY_MILLIS);
                    locationTrackingExpiredAlarmManager.setAlarm(TRACKING_EXPIRE_TIME_MILLIS);
                    return Tasks.forResult(null);
                });
    }

    private GeofencingRequest buildRequestToTrack(Schedule scheduleWithLocationToTrack) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(this.buildLocationToTrack(scheduleWithLocationToTrack))
                .build();
    }

    private Geofence buildLocationToTrack(Schedule scheduleWithLocationToTrack) {
        GeoPoint locationCoords = scheduleWithLocationToTrack.getLocation();

        return new Geofence.Builder()
                .setRequestId(scheduleWithLocationToTrack.getUid())
                .setCircularRegion(locationCoords.getLatitude(), locationCoords.getLongitude(),
                        TRACKING_RADIUS_METERS)
                .setExpirationDuration(TRACKING_EXPIRE_TIME_MILLIS)
                .setNotificationResponsiveness(MAX_NOTIFY_DELAY_MILLIS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(context, LocationBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
