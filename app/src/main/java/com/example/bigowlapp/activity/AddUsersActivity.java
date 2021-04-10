package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bigowlapp.R;


public class AddUsersActivity extends BigOwlActivity {
    private static final int CONTACT_PERMISSION_CODE = 1;
    private Button btnContacts;
    private Button btnPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    protected void initialize() {
        btnContacts = findViewById(R.id.btnContacts);

        btnContacts.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(AddUsersActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(AddUsersActivity.this, SearchContactsToSupervise.class);
                startActivity(i);
            } else {
                requestContactPermission();
            }
        });

        btnPhone = findViewById(R.id.btnPhone);

        btnPhone.setOnClickListener(v -> {
            Intent i = new Intent(AddUsersActivity.this, SearchContactsByPhone.class);
            startActivity(i);
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_add_users;
    }

    private void requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission is required to read phone Contacts")
                    .setPositiveButton("Ok", (dialogInterface, which) ->
                            ActivityCompat.requestPermissions(AddUsersActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AddUsersActivity.this, SearchContactsToSupervise.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}