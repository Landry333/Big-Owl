package com.example.bigowlapp.utils;

import android.content.Context;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;


public class GeoLocationFormatter {
    public String formatLocation(Context context, GeoPoint geoPoint) {
        try {
            return new Geocoder(context).getFromLocation(
                    geoPoint.getLatitude(),
                    geoPoint.getLongitude(),
                    1).get(0).getAddressLine(0);
        } catch (Exception e) {
            Log.e("BigOwl", "GeoPoint formatter exception", e);
        }
        return "ERROR";
    }

}
