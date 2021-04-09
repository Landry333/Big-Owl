package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.bigowlapp.R;

public class InvitationConfirmationActivity extends BigOwlActivity {

    private Button returnToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnToHome = findViewById(R.id.returnHome);

        returnToHome.setOnClickListener(v -> {
            Intent intent = new Intent(InvitationConfirmationActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_invitation_confirmation;
    }
}