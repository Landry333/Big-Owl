package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.bigowlapp.utils.Constants.SPLASH_DURATION;

public class WelcomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        PermissionsHelper permissionsHelper = new PermissionsHelper(this);

        List<String> permissionsToRequest = new ArrayList<>(Arrays.asList(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_NUMBERS);
        }

        // Only login a user if they have accepted the necessary permissions
        if (permissionsHelper.checkForMissingPermissions(permissionsToRequest).isEmpty()) {
            loadLoginPageWithDelay();
        } else {
            permissionsHelper.requestMissingPermissions(permissionsToRequest,
                    "You have to provide SMS and Phone permissions to use the app!");
        }

    }

    private void loadLoginPageWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(WelcomePageActivity.this, LoginPageActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_DURATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean granted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }

        if (grantResults.length > 0 && granted) {
            loadLoginPageWithDelay();
        } else {
            Toast.makeText(this, "You must accept all permissions to use the app.", Toast.LENGTH_SHORT).show();
        }
    }
}