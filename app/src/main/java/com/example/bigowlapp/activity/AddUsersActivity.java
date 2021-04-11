package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.PermissionsHelper;

import java.util.Collections;
import java.util.List;


public class AddUsersActivity extends BigOwlActivity {
    private Button btnContacts;
    private Button btnPhone;

    private PermissionsHelper permissionsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionsHelper = new PermissionsHelper(this);

        initialize();
    }

    protected void initialize() {
        btnContacts = findViewById(R.id.btnContacts);
        btnPhone = findViewById(R.id.btnPhone);


        btnContacts.setOnClickListener(v -> {
            List<String> permissionsToCheck = Collections.singletonList(Manifest.permission.READ_CONTACTS);

            if (permissionsHelper.checkForMissingPermissions(permissionsToCheck).isEmpty()) {
                Intent i = new Intent(AddUsersActivity.this, SearchContactsToSupervise.class);
                startActivity(i);
            } else {
                permissionsHelper.requestMissingPermissions(permissionsToCheck, "Permission is required to read phone Contacts.");
            }
        });

        btnPhone.setOnClickListener(v -> {
            Intent i = new Intent(AddUsersActivity.this, SearchContactsByPhone.class);
            startActivity(i);
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_add_users;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = permissionsHelper.handlePermissionResult(requestCode, grantResults);

        if (granted) {
            Intent i = new Intent(AddUsersActivity.this, SearchContactsToSupervise.class);
            startActivity(i);
        }
    }
}