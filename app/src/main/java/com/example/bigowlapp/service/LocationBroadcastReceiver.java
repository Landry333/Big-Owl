package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // valuable data from geofence result
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        List<String> geofenceIdList = triggeringGeofences.stream()
                .map(Geofence::getRequestId)
                .distinct()
                .collect(Collectors.toList());

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.CORRECT_LOCATION, context);
            // User was successfully detected in desired location, so no more tracking needed
            this.removeLocationTracking(context, geofenceIdList);

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.WRONG_LOCATION, context);
        } else {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.NOT_DETECTED, context);
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

    private void updateUserLocatedStatus(List<String> scheduleUidList, Attendance.LocatedStatus locatedStatusToAdd, Context context) {
        repositoryFacade.getScheduleRepository()
                .getDocumentsByListOfUid(scheduleUidList, Schedule.class)
                .observeForever(schedules -> {
                    for (Schedule schedule : schedules) {
                        Attendance userAttendance = schedule
                                .getUserScheduleResponseMap()
                                .get(repositoryFacade.getCurrentUserUid())
                                .getAttendance();

                        userAttendance.setAuthenticationTime(Timestamp.now());

                        // Avoid marking user as not in correct location if they happen to leave the geofence
                        // instantly after they are within it.
                        if (userAttendance.getScheduleLocated() != Attendance.LocatedStatus.CORRECT_LOCATION) {
                            userAttendance.setScheduleLocated(locatedStatusToAdd);
                        }

                        repositoryFacade.getScheduleRepository()
                                .updateDocument(schedule.getUid(), schedule);

                        if (userAttendance.getScheduleLocated() == Attendance.LocatedStatus.CORRECT_LOCATION) {
                            for (String scheduleID : scheduleUidList) {
                                AuthenticatorByPhoneNumber authenticationByPhoneNumber = new AuthenticatorByPhoneNumber(context);
                                authenticationByPhoneNumber.authenticate(scheduleID);
                            }
                        }
                    }
                });
    }
}
