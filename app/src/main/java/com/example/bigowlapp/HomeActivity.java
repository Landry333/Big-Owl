package com.example.bigowlapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.example.bigowlapp.Fragments.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.PrivateKey;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogOut, btnMonitoringGroup, btnSupervisedGroup, btnSearchUsers, btnMonitoringList, SendSmsInvitation;
    private FirebaseAuth m_FirebaseAuth;
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
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });

            btnMonitoringGroup = findViewById(R.id.btnMonitoringGroup);

            btnMonitoringGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(HomeActivity.this, MonitoringGroupListActivity.class);

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

            btnSearchUsers = findViewById(R.id.btnSearchUsers);

            btnSearchUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(HomeActivity.this, SearchContactsToSupervise.class);
                    startActivity(i);
                }
            });



            SendSmsInvitation = findViewById(R.id.SendSmsInvitation);

            SendSmsInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, SendSmsInvitationActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
}