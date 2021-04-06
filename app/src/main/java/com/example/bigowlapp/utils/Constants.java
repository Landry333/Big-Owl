package com.example.bigowlapp.utils;

public interface Constants {
    // constants for time conversion
    int MINUTE_TO_SECONDS = 60;
    int SECONDS_TO_MILLIS = 1000;
    int MINUTE_TO_MILLIS = MINUTE_TO_SECONDS * SECONDS_TO_MILLIS;

    // Request Codes for onActivityResult action differentiation
    int REQUEST_CODE_LOCATION = 1;
    // Splash screen time out in milliseconds
    int SPLASH_DURATION = 2000;

    /**
     * Represent the request code value when making a broadcast receiver. By default you just use 0
     * if you have no other use for it. Define other request codes if a receiver can take and must
     * hand more than one behaviors at a time.
     */
    int DEFAULT_RECEIVER_REQUEST_CODE = 0;
}
