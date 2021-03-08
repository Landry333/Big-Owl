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
    private static final int DEFAULT_TRACKING_RADIUS_METERS = 300;
    private static final long DEFAULT_TRACKING_TIME_MILLISECONDS = 30L * Constants.MINUTE_TO_MILLISECONDS;
    // use 0 for instant response, and allow delay for better battery life
    private static final int DEFAULT_MAX_NOTIFY_DELAY_MILLISECONDS = 1 * Constants.MINUTE_TO_MILLISECONDS;

    private final Context context;
    private final GeofencingClient geofencingClient;
    private final PeriodicLocationCheckAlarmManager locationCheckAlarmManager;
    private PendingIntent geofencePendingIntent;

    public ScheduledLocationTrackingManager(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
        locationCheckAlarmManager = new PeriodicLocationCheckAlarmManager(context);
    }

    public Task<Void> addNewScheduledLocationToTrack(Schedule scheduleWithLocationToTrack) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Tasks.forException(new SecurityException("Requires " + Manifest.permission.ACCESS_FINE_LOCATION + " permission."));
        }

        return geofencingClient
                .addGeofences(buildRequestToTrack(scheduleWithLocationToTrack), getGeofencePendingIntent())
                .onSuccessTask(aVoid -> {
                    locationCheckAlarmManager.setAlarm(DEFAULT_MAX_NOTIFY_DELAY_MILLISECONDS,
                            DEFAULT_TRACKING_TIME_MILLISECONDS);
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
                        DEFAULT_TRACKING_RADIUS_METERS)
                .setExpirationDuration(DEFAULT_TRACKING_TIME_MILLISECONDS)
                .setNotificationResponsiveness(DEFAULT_MAX_NOTIFY_DELAY_MILLISECONDS)
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
