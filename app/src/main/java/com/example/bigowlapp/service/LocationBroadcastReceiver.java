package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This BroadcastReceiver gets notified when the user exits/enters a location when location is being
 * tracked. This is a result of geofences added using
 * {@link com.example.bigowlapp.utils.ScheduledLocationTrackingManager}
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

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
            Log.e("BigOwl", errorMessage);
            return;
        }

        // valuable data from geofence result
        Location currentUserLocation = geofencingEvent.getTriggeringLocation();
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        List<String> geofenceIdList = triggeringGeofences.stream()
                .map(Geofence::getRequestId)
                .distinct()
                .collect(Collectors.toList());
        Log.e("BigOwl", "SCHEDULE IDS THAT TRIGGERED THIS ARE: " + geofenceIdList);
        List<String> testOnlyIdList = Collections.singletonList("4laPgh1xNIy8CDpnohDV");


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if (Constants.ENABLE_TESTING_TOGGLE) {
                Toast.makeText(context, "YOU HAVE SUCCESSFULLY ARRIVED TO YOUR LOCATION!", Toast.LENGTH_LONG).show();
                Log.e("BigOwl", "YOU ENTERED THE LOCATION");
            }

            if (Constants.ENABLE_TESTING_TOGGLE) {
                this.updateUserLocatedStatus(testOnlyIdList, Attendance.LocatedStatus.CORRECT_LOCATION);
            } else {
                this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.CORRECT_LOCATION);
            }

            // User was successfully detected in desired location, so no more tracking needed
            this.removeLocationTracking(context, geofenceIdList)
                    .addOnSuccessListener(aVoid -> {
                        Log.e("BigOwl", "Entered LOCATION TRACKING SUCCESSFULLY REMOVED");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BigOwl", "FAILED TO REMOVE LOCATIONS");
                    });

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            if (Constants.ENABLE_TESTING_TOGGLE) {
                Toast.makeText(context, "YOU HAVE YET TO ARRIVE TO THE NECESSARY LOCATION", Toast.LENGTH_LONG).show();
                Log.e("BigOwl", "YOU EXITED THE LOCATION");
            }

            if (Constants.ENABLE_TESTING_TOGGLE) {
                this.updateUserLocatedStatus(testOnlyIdList, Attendance.LocatedStatus.WRONG_LOCATION);
            } else {
                this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.WRONG_LOCATION);
            }

        } else {
            Log.e("BigOwl", "Location Detection has failed");
            if (Constants.ENABLE_TESTING_TOGGLE) {
                this.updateUserLocatedStatus(testOnlyIdList, Attendance.LocatedStatus.NOT_DETECTED);
            } else {
                this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.NOT_DETECTED);
            }
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
                        schedule.getUserScheduleResponseMap().get(userUid)
                                .getAttendance()
                                .setScheduleLocated(locatedStatusToAdd);

                        repositoryFacade.getScheduleRepository()
                                .updateDocument(schedule.getUid(), schedule);
                    }
                });
    }
}
