package com.example.bigowlapp.service;

import android.content.Context;
import android.content.Intent;

import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationBroadcastReceiverTest {

    @Mock
    private RepositoryFacade mockRepositoryFacade;
    @Mock
    private Intent mockGefenceIntent;
    @Mock
    private GeofencingEvent mockGeofencingEvent;
    @Mock
    private Context mockContext;


    private LocationBroadcastReceiver locationBroadcastReceiver;

    @Before
    public void setUp() throws Exception {
        locationBroadcastReceiver = new LocationBroadcastReceiver();
        locationBroadcastReceiver.setRepositoryFacade(mockRepositoryFacade);
        locationBroadcastReceiver.setGeofencingEvent(mockGeofencingEvent);
    }

    @Test
    public void onReceiveGetsGeofencingError() {
        when(mockGeofencingEvent.hasError()).thenReturn(true);
        when(mockGeofencingEvent.getErrorCode()).thenReturn(GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION);

        locationBroadcastReceiver.onReceive(mockContext, mockGefenceIntent);
        verify(mockGeofencingEvent, times(1)).getErrorCode();
    }
}