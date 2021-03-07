package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This BroadcastReceiver gets notified when the user exits/enters a location when location is being
 * tracked. This is a result of geofences added using
 * {@link com.example.bigowlapp.utils.ScheduledLocationTrackingManager}
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = LocationBroadcastReceiver.class.getName();

    RepositoryFacade repositoryFacade;

    public LocationBroadcastReceiver() {
        super();
        repositoryFacade = RepositoryFacade.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // valuable data from geofence result
        // TODO: if not needed remove, else uncomment for users current location
        //  Location currentUserLocation = geofencingEvent.getTriggeringLocation();
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        List<String> geofenceIdList = triggeringGeofences.stream()
                .map(Geofence::getRequestId)
                .distinct()
                .collect(Collectors.toList());

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.CORRECT_LOCATION);

            // User was successfully detected in desired location, so no more tracking needed
            this.removeLocationTracking(context, geofenceIdList)
                    .addOnSuccessListener(aVoid ->
                            Log.e(TAG, "Entered LOCATION TRACKING SUCCESSFULLY REMOVED"))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "FAILED TO REMOVE LOCATIONS"));

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.WRONG_LOCATION);
        } else {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.NOT_DETECTED);
        }
    }

    private Task<Void> removeLocationTracking(Context context, List<String> geofencesToRemoveIdList) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        return geofencingClient.removeGeofences(geofencesToRemoveIdList);
    }

    private void updateUserLocatedStatus(List<String> scheduleUidList, Attendance.LocatedStatus locatedStatusToAdd) {
        repositoryFacade.getScheduleRepository()
                .getDocumentsByListOfUid(scheduleUidList, Schedule.class)
                .observeForever(schedules -> {
                    String userUid = repositoryFacade.getAuthRepository()
                            .getCurrentUser().getUid();

                    for (Schedule schedule : schedules) {
                        Attendance userAttendance = schedule
                                .getUserScheduleResponseMap()
                                .get(userUid)
                                .getAttendance();
                        userAttendance.setScheduleLocated(locatedStatusToAdd);
                        userAttendance.setAuthenticationTime(Timestamp.now());

                        repositoryFacade.getScheduleRepository()
                                .updateDocument(schedule.getUid(), schedule);
                    }
                });
    }
}
