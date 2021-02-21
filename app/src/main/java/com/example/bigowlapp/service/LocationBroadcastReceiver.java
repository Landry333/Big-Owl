package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * This BroadcastReceiver gets notified when the user exits/enters a location when location is being
 * tracked.
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    public LocationBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // When location changes occur, this will run and handle location info
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("BigOwl", errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Location location = geofencingEvent.getTriggeringLocation();

        Log.e("BigOwl", "the location that has triggered this BR is " + location.toString());

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // TODO: entering location case
            Toast.makeText(context, "PERSON HAS ENTERED LOCATION", Toast.LENGTH_LONG).show();

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // TODO: exiting location case
            Toast.makeText(context, "PERSON HAS EXITED LOCATION", Toast.LENGTH_LONG).show();

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        } else {
            Log.e("BigOwl", "Location Detection has failed");
        }
    }
}
