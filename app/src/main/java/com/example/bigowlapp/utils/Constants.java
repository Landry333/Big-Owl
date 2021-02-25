package com.example.bigowlapp.utils;

public interface Constants {
    // Future Toggles
    boolean ENABLE_TESTING_TOGGLE = true;

    // constants for time conversion
    int MINUTE_TO_SECONDS = 60;
    int SECONDS_TO_MILLISECONDS = 1000;
    int MINUTE_TO_MILLISECONDS = MINUTE_TO_SECONDS * SECONDS_TO_MILLISECONDS;

    // Request Codes for onActivityResult action differentiation
    int REQUEST_CODE_LOCATION = 1;
    // Splash screen time out in milliseconds
    int SPLASH_DURATION = 2000;
}
