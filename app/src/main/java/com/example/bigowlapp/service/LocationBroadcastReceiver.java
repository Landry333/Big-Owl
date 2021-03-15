package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.AuthenticatorByPhoneNumber;
import com.example.bigowlapp.utils.LocationTrackingExpiredAlarmManager;
import com.example.bigowlapp.utils.PeriodicLocationCheckAlarmManager;
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

    private final RepositoryFacade repositoryFacade;

    public LocationBroadcastReceiver() {
        super();
        repositoryFacade = RepositoryFacade.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Reached LocationBroadcastReceiver ligne 1", Toast.LENGTH_LONG).show();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Toast.makeText(context, "Reached LocationBroadcastReceiver: Geofencing Error", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "Found correct location and started authentication", Toast.LENGTH_LONG).show();
            for (String scheduleID: geofenceIdList){
                AuthenticatorByPhoneNumber authenticationByPhoneNumber = new AuthenticatorByPhoneNumber(context);
                authenticationByPhoneNumber.authenticate(scheduleID);
                Log.e("Authentication Start scheduleID", scheduleID);
            }

            // User was successfully detected in desired location, so no more tracking needed
            this.removeLocationTracking(context, geofenceIdList);

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.WRONG_LOCATION);
            Toast.makeText(context, "WRONG LOCATION DETECTED", Toast.LENGTH_LONG).show();
        } else {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.NOT_DETECTED);
            Toast.makeText(context, "LOCATION NOT DETECTED", Toast.LENGTH_LONG).show();
        }
    }

    private Task<Void> removeLocationTracking(Context context, List<String> geofencesToRemoveIdList) {
        // no need for periodic location checking anymore
        PeriodicLocationCheckAlarmManager locationCheckAlarmManager = new PeriodicLocationCheckAlarmManager(context);
        locationCheckAlarmManager.cancelPeriodicLocationCheck();

        // no need to check tracking expiration anymore
        LocationTrackingExpiredAlarmManager locationTrackingExpiredAlarmManager =
                new LocationTrackingExpiredAlarmManager(context);
        locationTrackingExpiredAlarmManager.cancelExpirationAlarm();

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

                        // If the user was already detected to be in the location, no need to
                        // update the database anymore.
                        if (userAttendance.getScheduleLocated() == Attendance.LocatedStatus.CORRECT_LOCATION) {
                            return;
                        }

                        userAttendance.setScheduleLocated(locatedStatusToAdd);
                        userAttendance.setAuthenticationTime(Timestamp.now());
                        repositoryFacade.getScheduleRepository()
                                .updateDocument(schedule.getUid(), schedule);
                    }
                });
    }
}
