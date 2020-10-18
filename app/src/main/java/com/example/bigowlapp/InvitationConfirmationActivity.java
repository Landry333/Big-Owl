package com.example.bigowlapp;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InvitationConfirmationActivity extends AppCompatActivity {

    private Button returnToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_confirmation);

        returnToHome = findViewById(R.id.returnHome);

        returnToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvitationConfirmationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}