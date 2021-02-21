package com.example.bigowlapp.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.example.bigowlapp.service.LocationBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.util.UUID;

public class ScheduledLocationTrackingManager {

    private static final int DEFAULT_TRACKING_RADIUS_METERS = 100;
    private static final long DEFAULT_TRACKING_TIME_MILLISECONDS = 30 * Constants.MINUTE_TO_MILLISECONDS;
    // use 0 for instant response, and allow delay for better battery life
    private static final int DEFAULT_MAX_NOTIFY_DELAY_MILLISECONDS = 5 * Constants.MINUTE_TO_MILLISECONDS;
    private static final int NO_NOTIFY_DELAY = 0;

    private Context context;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    public ScheduledLocationTrackingManager(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public Task<Void> addNewLocationToTrack(GeoPoint locationCoords) {
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

        return geofencingClient.addGeofences(buildRequestToTrack(locationCoords), getGeofencePendingIntent());
    }

    private GeofencingRequest buildRequestToTrack(GeoPoint locationCoords) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(this.buildLocationToTrack(locationCoords))
                .build();
    }

    private Geofence buildLocationToTrack(GeoPoint locationCoords) {
        return new Geofence.Builder()
                // TODO: correctly setup ids, should correspond with a schedule
                .setRequestId(UUID.randomUUID().toString())
                .setCircularRegion(locationCoords.getLatitude(), locationCoords.getLongitude(),
                        DEFAULT_TRACKING_RADIUS_METERS)
                .setExpirationDuration(DEFAULT_TRACKING_TIME_MILLISECONDS)
                .setNotificationResponsiveness(NO_NOTIFY_DELAY)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
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
