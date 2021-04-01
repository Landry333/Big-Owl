package com.example.bigowlapp.utils;

import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;

public class GeoLocationFormatter {
    public static String formatLocation(GeoPoint geoPoint) {
        try {
            return new Geocoder(ApplicationProvider.getApplicationContext()).getFromLocation(
                    geoPoint.getLatitude(),
                    geoPoint.getLongitude(),
                    1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

}
