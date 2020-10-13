package com.example.bigowlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SwitchUserType extends AppCompatActivity {
    Button switchtype;
    FirebaseAuth m_FirebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthStateListener;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_listusers);
        try {
            switchtype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //FirebaseAuth.getInstance().signOut();
                    if (type == "monitoring")
                        setContentView(R.layout.activity_monitoring_user_login);
                    if (type == "supervised")
                        setContentView(R.layout.activity_supervised_user_login);
                    Intent i = new Intent(SwitchUserType.this, MainActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {

        }
    }
}