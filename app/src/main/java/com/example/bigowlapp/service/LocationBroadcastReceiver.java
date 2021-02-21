package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This BroadcastReceiver gets notified when the user exits/enters a location when location is being
 * tracked. This is a result of geofences added using
 * {@link com.example.bigowlapp.utils.ScheduledLocationTrackingManager}
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    public LocationBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("BigOwl", errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Location currentUserLocation = geofencingEvent.getTriggeringLocation();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        List<String> geofenceIdList = triggeringGeofences.stream()
                .map(Geofence::getRequestId)
                .collect(Collectors.toList());

        Log.e("BigOwl", "SCHEDULE IDS THAT TRIGGERED THIS ARE: " + geofenceIdList);

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // TODO: entering location case
            Toast.makeText(context, "PERSON HAS ENTERED LOCATION", Toast.LENGTH_LONG).show();
            Log.e("BigOwl", "YOU ENTERED THE LOCATION");

            // User was successfully detected in desired location, so no more tracking needed
            this.removeLocationTracking(context, triggeringGeofences)
                    .addOnSuccessListener(aVoid -> {
                        Log.e("BigOwl", "Entered LOCATION TRACKING SUCCESSFULLY REMOVED");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BigOwl", "FAILED TO REMOVE LOCATIONS");
                    });

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // TODO: exiting location case
            Toast.makeText(context, "PERSON HAS EXITED LOCATION", Toast.LENGTH_LONG).show();
            Log.e("BigOwl", "YOU EXITED THE LOCATION");

        } else {
            Log.e("BigOwl", "Location Detection has failed");
        }
    }

    private Task<Void> removeLocationTracking(Context context, List<Geofence> geofencesToRemove) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        List<String> geofenceIdList = geofencesToRemove.stream()
                .map(Geofence::getRequestId)
                .collect(Collectors.toList());
        return geofencingClient.removeGeofences(geofenceIdList);
    }
}
