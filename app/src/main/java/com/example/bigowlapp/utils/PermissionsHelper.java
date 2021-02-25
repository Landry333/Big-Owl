package com.example.bigowlapp.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This helper can be used when needing to handle calls for permissions
 * without bloating an Android UI class, and to reduced repeated code.
 */
public class PermissionsHelper {
    public static final int MULTIPLE_PERMISSIONS_CODE = 1;

    private final Activity activity;

    public PermissionsHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Used to check for missing permissions and request for those that are missing
     * @param permissionsToCheck the permissions that are to be verified
     */
    public void requestMissingPermissions(List<String> permissionsToCheck) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor);
        }
    }

    /**
     * Checks for and requests the mission, but also provides a reason the permissions are needed
     * @param permissionsToCheck the permissions that are to be verified
     * @param justification the reasons these permissions will be needed
     */
    public void requestMissingPermissions(List<String> permissionsToCheck, String justification) {
        List<String> permissionsToAskFor = this.checkForMissingPermissions(permissionsToCheck);

        if (!permissionsToAskFor.isEmpty()) {
            this.requestPermissions(permissionsToAskFor, justification);
        }
    }

    /**
     * Use in 'Activity.onRequestPermissionsResult' to handle what happens when a user
     * accepts or rejects a set of requested permissions
     * @param grantResults the permission result from 'Activity.onRequestPermissionsResult'
     */
    public void handlePermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission(s) GRANTED", Toast.LENGTH_SHORT).show();
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
        ActivityCompat.requestPermissions(activity, permissionsToAskFor.toArray(new String[0]), MULTIPLE_PERMISSIONS_CODE);
    }

    public void requestPermissions(List<String> permissionsToAskFor, String justification) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission(s) needed")
                .setMessage(justification)
                .setPositiveButton("Ok", (dialogInterface, which) -> requestPermissions(permissionsToAskFor))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
