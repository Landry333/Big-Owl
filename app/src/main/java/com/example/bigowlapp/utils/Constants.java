package com.example.bigowlapp.utils;

public class Constants {

    private Constants() {
        // This shouldn't have any instance
    }

    // constants for time conversion
    public static final int MINUTE_TO_SECONDS = 60;
    public static final int SECONDS_TO_MILLIS = 1000;
    public static final int MINUTE_TO_MILLIS = MINUTE_TO_SECONDS * SECONDS_TO_MILLIS;

    // Request Codes for onActivityResult action differentiation
    public static final int REQUEST_CODE_LOCATION = 1;
    // Splash screen time out in milliseconds
    public static final int SPLASH_DURATION = 2000;

    /**
     * Represent the request code value when making a broadcast receiver. By default you just use 0
     * if you have no other use for it. Define other request codes if a receiver can take and must
     * hand more than one behaviors at a time.
     */
    public static final int DEFAULT_RECEIVER_REQUEST_CODE = 0;
}
