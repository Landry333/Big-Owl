package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bigowlapp.R;


public class AddUsers extends AppCompatActivity {
    private final int CONTACT_PERMISSION_CODE = 1;
    Button btnContacts, btnPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        initialize();
    }

    protected void initialize() {
        btnContacts = findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddUsers.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(HomePageActivity.this, "This permission is already granted", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AddUsers.this, SearchContactsToSupervise.class);
                    startActivity(i);
                } else {
                    requestContactPermission();
                }
            }
        });

        btnPhone = findViewById(R.id.btnPhone);

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddUsers.this, SearchContactsByPhone.class);
                startActivity(i);
            }
        });
    }

    private void requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission is required to read phone Contacts")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(AddUsers.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AddUsers.this, SearchContactsToSupervise.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}