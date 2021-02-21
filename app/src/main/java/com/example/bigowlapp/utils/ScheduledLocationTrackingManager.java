package com.example.bigowlapp.utils;

import android.content.Context;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
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

    public ScheduledLocationTrackingManager(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public GeofencingRequest buildRequestToTrack(GeoPoint locationCoords) {
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
}
