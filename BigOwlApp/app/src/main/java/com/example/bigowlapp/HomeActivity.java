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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.PrivateKey;

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

    @Override
    protected void onStart() {
        super.onStart();

        UserRepository r = new UserRepository();
        MutableLiveData<User> userData = r.getUserByPhoneNumber("+16505554567");

        Log.d("FIREBASE", "Pekora = :3");

        // Create the observer which updates the UI.
        final Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User user) {
                Log.d("FIREBASE", "Pekora2 = :3");
                if (user != null) {
                    Log.d("FIREBASE", "user phone = " + user.getPhoneNumber());
                    Log.d("FIREBASE", "user email = " + user.getEmail());
                    Log.d("FIREBASE", "user id = " + user.getuId());
                    Log.d("FIREBASE", "user imageUrl = " + user.getProfileImage());
                } else {
                    Log.d("FIREBASE", "ooooooofffffff");
                }
            }
        };

        //userData.observeForever(userObserver);
        //Log.d("FIREBASE", "userdata PEKO = " + userData.getValue());
        userData.observeForever(new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User user) {
                Log.d("FIREBASE", "Pekora2 = :3");
                if (user != null) {
                    Log.d("FIREBASE", "user phone = " + user.getPhoneNumber());
                    Log.d("FIREBASE", "user email = " + user.getEmail());
                    Log.d("FIREBASE", "user id = " + user.getuId());
                    Log.d("FIREBASE", "user imageUrl = " + user.getProfileImage());
                } else {
                    Log.d("FIREBASE", "ooooooofffffff");
                }
            }
    });
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
        } catch (Exception ex) {

        }
    }
}