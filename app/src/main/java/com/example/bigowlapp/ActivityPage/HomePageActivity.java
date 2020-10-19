package com.example.bigowlapp.ActivityPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bigowlapp.MonitoringGroupPageActivity;
import com.example.bigowlapp.R;
import com.example.bigowlapp.SearchContactsToSupervise;
import com.example.bigowlapp.SendSmsInvitationActivity;
import com.example.bigowlapp.SupervisedGroupListActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button btnLogOut, sendSmsInvitation, btnSearchUsers, btnMonitoringGroup, btnSupervisedGroup, btnMonitoringList;
    FirebaseAuth m_FirebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthStateListener;
    private final int CONTACT_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initialize() {
        try {
            btnLogOut = findViewById(R.id.Logout);

            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(HomeActivity.this, SignUpActivity.class);
                    startActivity(i);
                }
            });

            sendSmsInvitation = findViewById(R.id.SendSmsInvitation);

            sendSmsInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, SendSmsInvitationActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            btnMonitoringGroup = findViewById(R.id.btnMonitoringGroup);

            btnMonitoringGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(HomeActivity.this, MonitoringGroupPageActivity.class);
                    startActivity(i);
                }
            });

            btnSupervisedGroup = findViewById(R.id.btnSupervisedGroup);

            btnSupervisedGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(HomeActivity.this, SupervisedGroupListActivity.class);
                    startActivity(i);
                }
            });

            btnSearchUsers = findViewById(R.id.btnPhoneContacts);

            btnSearchUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                        //Toast.makeText(HomeActivity.this, "This permission is already granted", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(HomeActivity.this, SearchContactsToSupervise.class);
                        startActivity(i);
                    } else {
                        requestContactPermission();
                    }
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    //TODO refactor to another class file
    private void requestContactPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission is required to read phone Contacts")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.READ_CONTACTS},CONTACT_PERMISSION_CODE );
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
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS},CONTACT_PERMISSION_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(HomeActivity.this, SearchContactsToSupervise.class);
                startActivity(i);
            } else{
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}