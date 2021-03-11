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
}
