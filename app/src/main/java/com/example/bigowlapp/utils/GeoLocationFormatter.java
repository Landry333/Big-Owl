package com.example.bigowlapp.utils;

import android.content.Context;
import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;

public class GeoLocationFormatter {
    public static String formatLocation(Context context, GeoPoint geoPoint) {
        try {
            return new Geocoder(context).getFromLocation(
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
