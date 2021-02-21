package com.example.bigowlapp.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.service.LocationBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

public class ScheduledLocationTrackingManager {

    private static final int DEFAULT_TRACKING_RADIUS_METERS = 150;
    private static final long DEFAULT_TRACKING_TIME_MILLISECONDS = 30 * Constants.MINUTE_TO_MILLISECONDS;
    // use 0 for instant response, and allow delay for better battery life
    private static final int DEFAULT_MAX_NOTIFY_DELAY_MILLISECONDS = 5 * Constants.MINUTE_TO_MILLISECONDS;
    private static final int NO_NOTIFY_DELAY = 0;

    private final Context context;
    private final GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    public ScheduledLocationTrackingManager(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public Task<Void> addNewScheduledLocationToTrack(Schedule scheduleWithLocationToTrack) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle missing permissions case
            // Consider calling ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        return geofencingClient.addGeofences(
                buildRequestToTrack(scheduleWithLocationToTrack), getGeofencePendingIntent());
    }

    private GeofencingRequest buildRequestToTrack(Schedule scheduleWithLocationToTrack) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(this.buildLocationToTrack(scheduleWithLocationToTrack))
                .build();
    }

    private Geofence buildLocationToTrack(Schedule scheduleWithLocationToTrack) {
        if (scheduleWithLocationToTrack.getUid() == null) {
            Log.e("BigOwl", "Schedule Uid not found. Tracking a schedule requires uid!");
            return null;
        }

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
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
