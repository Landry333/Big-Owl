package com.example.bigowlapp.ActivityPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bigowlapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button btnLogOut;
    FirebaseAuth m_FirebaseAuth;
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
                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
}