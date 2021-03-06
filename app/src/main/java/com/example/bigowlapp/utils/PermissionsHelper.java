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

import com.example.bigowlapp.R;

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
        this.requestMissingPermissions(permissionsToCheck, REQUEST_DEFAULT);
    }

    /**
     * Used to check for missing permissions and request for those that are missing.
     *
     * @param permissionsToCheck the permissions that are to be verified
     * @param requestCode        alter how the permission result is handled using a REQUEST
     */
    public void requestMissingPermissions(List<String> permissionsToCheck, int requestCode) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor, requestCode);
        }
    }

    /**
     * Checks for and requests the permission, but also provides a reason the permissions are needed
     *
     * @param permissionsToCheck the permissions that are to be verified
     * @param justification      the reasons these permissions will be needed
     */
    public void requestMissingPermissions(List<String> permissionsToCheck, CharSequence justification) {
        this.requestMissingPermissions(permissionsToCheck, justification, REQUEST_DEFAULT);
    }

    /**
     * Checks for and requests the permission, but also provides a reason the permissions are needed
     *
     * @param permissionsToCheck the permissions that are to be verified
     * @param justification      the reasons these permissions will be needed
     * @param requestCode        alter how the permission result is handled using a REQUEST
     */
    public void requestMissingPermissions(List<String> permissionsToCheck, CharSequence justification, int requestCode) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor, justification, requestCode);
        }
    }

    /**
     * Simple method that can be used to ask for FINE_LOCATION andBACKGROUND_LOCATION and handles
     * all possible scenarios regarding location/background location request
     */
    public void requestLocationAndBackgroundLocationPermissions() {
        CharSequence justification = activity.getResources().getText(R.string.general_location_justification);
        this.requestMissingPermissions(
                Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION),
                justification,
                PermissionsHelper.REQUEST_ALSO_REQUEST_BACKGROUND_LOCATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                !this.isMissingPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            this.requestBackgroundLocationPermissions();
        }
    }

    /**
     * Will request background permissions with a justification if it is missing.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void requestBackgroundLocationPermissions() {
        CharSequence justification = activity.getResources().getText(R.string.background_location_justification);
        this.requestMissingPermissions(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION), justification);
    }

    /**
     * Use in 'Activity.onRequestPermissionsResult' to handle what happens when a user
     * accepts or rejects a set of requested permissions.
     *
     * @param grantResults the permission result from 'Activity.onRequestPermissionsResult'
     */
    public boolean handlePermissionResult(int requestCode, int[] grantResults) {

        boolean granted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }

        if (requestCode == REQUEST_ALSO_REQUEST_BACKGROUND_LOCATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            handleBackgroundLocationPermissionResult(granted);
        }

        return granted;
    }

    public boolean handlePermissionResult(int requestCode, int[] grantResults, String denyMessage) {

        boolean granted = this.handlePermissionResult(requestCode, grantResults);

        if (!granted) {
            Toast.makeText(activity, denyMessage, Toast.LENGTH_SHORT).show();
        }

        return granted;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleBackgroundLocationPermissionResult(boolean isGranted) {
        if (isGranted) {
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

    private void requestPermissions(List<String> permissionsToAskFor, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissionsToAskFor.toArray(new String[0]), requestCode);
    }

    private void requestPermissions(List<String> permissionsToAskFor, CharSequence justification, int requestCode) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission(s) needed")
                .setMessage(justification)
                .setPositiveButton("Ok", (dialogInterface, which) -> requestPermissions(permissionsToAskFor, requestCode))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
