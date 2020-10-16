package com.example.bigowlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bigowlapp.Fragments.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogOut, btnMonitoringGroup, btnSupervisedGroup, btnSearchUsers, btnMonitoringList;

    private FirebaseAuth m_FirebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
    }

    protected void initialize()
    {
        try
        {
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


            /*
            btnSearchUsers = findViewById(R.id.btnSearchUsers);

            btnSearchUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(HomeActivity.this, MonitoringGroupListActivity.class);
                    startActivity(i);
                }
            });
            */


        }
        catch (Exception ex)
        {

        }
    }
}