package com.example.bigowlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button btnLogOut, sendSmsInvitation, btnMonitoringGroup, btnSupervisedGroup;
    FirebaseAuth m_FirebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthStateListener;

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
        } catch (Exception ex) {
        }
    }
}