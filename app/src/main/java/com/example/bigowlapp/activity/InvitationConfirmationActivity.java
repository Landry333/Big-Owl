package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bigowlapp.R;

public class InvitationConfirmationActivity extends BigOwlActivity {

    private Button returnToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnToHome = findViewById(R.id.returnHome);

        returnToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvitationConfirmationActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_invitation_confirmation;
    }

    @Override
    protected String getToolbarTitle() {
        return "Invite From Contact list";
    }
}