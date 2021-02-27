package com.example.bigowlapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This helper can be used when needing to handle calls for permissions
 * without bloating an Android UI class, and to reduced repeated code.
 */
public class PermissionsHelper {
    /**
     * Default request code to use for any simple permissions request.
     */
    public static final int REQUEST_DEFAULT = 1;

    /**
     * Use if Background Location permissions must also be requested after this permission request.
     */
    public static final int REQUEST_ALSO_REQUEST_BACKGROUND_LOCATION = 2;

    private final Activity activity;

    public PermissionsHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Used to check for missing permissions and request for those that are missing
     *
     * @param permissionsToCheck the permissions that are to be verified
     */
    public void requestMissingPermissions(List<String> permissionsToCheck) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor);
        }
    }

    public void requestMissingPermissions(List<String> permissionsToCheck, int requestCode) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor, requestCode);
        }
    }

    /**
     * Checks for and requests the mission, but also provides a reason the permissions are needed
     *
     * @param permissionsToCheck the permissions that are to be verified
     * @param justification      the reasons these permissions will be needed
     */
    public void requestMissingPermissions(List<String> permissionsToCheck, String justification) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor, justification);
        }
    }

    public void requestMissingPermissions(List<String> permissionsToCheck, String justification, int requestCode) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor, justification, requestCode);
        }
    }


    /**
     * Will request background permissions with a justification if it is missing.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void requestBackgroundLocationPermissions() {
        String reason = "This application tracks the location automatically at the scheduled time in the background so no manual check-in is needed.\n\n" +
                "In order to make this possible, you must allow location access 'all the time'";
        this.requestMissingPermissions(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION), reason);
    }

    /**
     * Use in 'Activity.onRequestPermissionsResult' to handle what happens when a user
     * accepts or rejects a set of requested permissions
     *
     * @param grantResults the permission result from 'Activity.onRequestPermissionsResult'
     */
    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_ALSO_REQUEST_BACKGROUND_LOCATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            handleBackgroundLocationPermissionResult(grantResults);
        } else {
            handleDefaultPermissionResult(grantResults);
        }
    }

    public void handleDefaultPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission(s) GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Permission(s) DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void handleBackgroundLocationPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestBackgroundLocationPermissions();
        } else {
            Toast.makeText(activity, "Permission(s) DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if the provided permissions are granted and returns the non-granted permissions
     *
     * @param permissionsToCheck the permissions that are to be verified
     * @return a filtered list where only the non-granted permissions remain
     */
    public List<String> checkForMissingPermissions(List<String> permissionsToCheck) {
        return permissionsToCheck.stream()
                .filter(this::isMissingPermission)
                .collect(Collectors.toList());
    }

    public boolean isMissingPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED;
    }

    public void requestPermissions(List<String> permissionsToAskFor) {
        ActivityCompat.requestPermissions(activity, permissionsToAskFor.toArray(new String[0]), REQUEST_DEFAULT);
    }

    public void requestPermissions(List<String> permissionsToAskFor, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissionsToAskFor.toArray(new String[0]), requestCode);
    }

    public void requestPermissions(List<String> permissionsToAskFor, String justification) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission(s) needed")
                .setMessage(justification)
                .setPositiveButton("Ok", (dialogInterface, which) -> requestPermissions(permissionsToAskFor))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    public void requestPermissions(List<String> permissionsToAskFor, String justification, int requestCode) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission(s) needed")
                .setMessage(justification)
                .setPositiveButton("Ok", (dialogInterface, which) -> requestPermissions(permissionsToAskFor, requestCode))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
