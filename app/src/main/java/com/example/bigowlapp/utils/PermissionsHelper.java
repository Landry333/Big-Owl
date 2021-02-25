package com.example.bigowlapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This helper can be used when needing to handle calls for permissions
 * without bloating an Android UI class, and to reduced repeated code
 */
public class PermissionsHelper {
    public static final int MULTIPLE_PERMISSIONS_CODE = 1;

    private final Activity activity;

    public PermissionsHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks if the provided permissions are granted and returns the non-granted permissions
     *
     * @param permissionsToCheck the permissions that are to be verified
     * @return a filtered list where only the non-granted permissions remain
     */
    public List<String> checkForMissingPermissions(String... permissionsToCheck) {
        return Arrays.stream(permissionsToCheck)
                .filter(this::isMissingPermission)
                .collect(Collectors.toList());
    }

    private boolean isMissingPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED;
    }

    public void requestLocationPermission(List<String> permissionsToAskFor) {

        Runnable permissionRequest = () ->
                ActivityCompat.requestPermissions(activity, permissionsToAskFor.toArray(new String[0]), MULTIPLE_PERMISSIONS_CODE);

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(activity)
                    .setTitle("Location permission needed")
                    .setMessage("Location detection is used to check whether you have arrived to a scheduled location")
                    .setPositiveButton("Ok", (dialogInterface, which) -> permissionRequest.run())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            permissionRequest.run();
        }
    }

    public void handleLocationPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permission GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Permission DENIED", Toast.LENGTH_SHORT).show();
        }
    }
}
