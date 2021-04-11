package com.example.bigowlapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.VisibleForTesting;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.AuthenticatorByPhoneNumber;
import com.example.bigowlapp.utils.LocationTrackingExpiredAlarmManager;
import com.example.bigowlapp.utils.PeriodicLocationCheckAlarmManager;
import com.google.android.gms.location.Geofence;
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
    private RepositoryFacade repositoryFacade;

    private GeofencingEvent geofencingEvent;
    private AuthenticatorByPhoneNumber authenticatorByPhoneNumber;
    private PeriodicLocationCheckAlarmManager locationCheckAlarmManager;
    private LocationTrackingExpiredAlarmManager locationTrackingExpiredAlarmManager;
    private GeofencingClient geofencingClient;

    public LocationBroadcastReceiver() {
        super();
        repositoryFacade = RepositoryFacade.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (geofencingEvent == null) {
            setDependencies(context, intent);
        }

        if (geofencingEvent.hasError()) {
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
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.CORRECT_LOCATION);
            // User was successfully detected in desired location, so no more tracking needed
            this.removeLocationTracking(geofenceIdList);

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.WRONG_LOCATION);
        } else {
            this.updateUserLocatedStatus(geofenceIdList, Attendance.LocatedStatus.NOT_DETECTED);
        }
    }

    private Task<Void> removeLocationTracking(List<String> geofencesToRemoveIdList) {
        // no need for periodic location checking anymore
        locationCheckAlarmManager.cancelPeriodicLocationCheck();
        // no need to check tracking expiration anymore
        locationTrackingExpiredAlarmManager.cancelExpirationAlarm();

        return geofencingClient.removeGeofences(geofencesToRemoveIdList);
    }

    private void updateUserLocatedStatus(List<String> scheduleUidList, Attendance.LocatedStatus locatedStatusToAdd) {
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
                            authenticatorByPhoneNumber.authenticate(schedule.getUid());
                        }
                    }
                });
    }

    private void setDependencies(Context context, Intent intent) {
        geofencingEvent = GeofencingEvent.fromIntent(intent);
        locationCheckAlarmManager = new PeriodicLocationCheckAlarmManager(context);
        locationTrackingExpiredAlarmManager = new LocationTrackingExpiredAlarmManager(context);
        geofencingClient = LocationServices.getGeofencingClient(context);
        authenticatorByPhoneNumber = new AuthenticatorByPhoneNumber(context);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setRepositoryFacade(RepositoryFacade repositoryFacade) {
        this.repositoryFacade = repositoryFacade;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setGeofencingEvent(GeofencingEvent geofencingEvent) {
        this.geofencingEvent = geofencingEvent;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setAuthenticatorByPhoneNumber(AuthenticatorByPhoneNumber authenticatorByPhoneNumber) {
        this.authenticatorByPhoneNumber = authenticatorByPhoneNumber;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setLocationCheckAlarmManager(PeriodicLocationCheckAlarmManager locationCheckAlarmManager) {
        this.locationCheckAlarmManager = locationCheckAlarmManager;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setLocationTrackingExpiredAlarmManager(LocationTrackingExpiredAlarmManager locationTrackingExpiredAlarmManager) {
        this.locationTrackingExpiredAlarmManager = locationTrackingExpiredAlarmManager;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setGeofencingClient(GeofencingClient geofencingClient) {
        this.geofencingClient = geofencingClient;
    }
}
