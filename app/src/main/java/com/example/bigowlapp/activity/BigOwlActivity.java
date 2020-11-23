package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.bigowlapp.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

public abstract class BigOwlActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ImageButton imgBtnOverflow, imgBtnSchedule, imgBtnUser, imgBtnNotification, imgBtnBigOwl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        imgBtnOverflow = findViewById(R.id.action_overflow);
        imgBtnOverflow.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.big_owl_overflow);
            popup.show();
        });

        imgBtnSchedule = findViewById(R.id.action_schedule);
        imgBtnSchedule.setOnClickListener(v -> {
            // startActivity(new Intent(this, Schedule?.class););
        });

        imgBtnUser = findViewById(R.id.action_user);
        imgBtnUser.setOnClickListener(v -> {
            // startActivity(new Intent(this, User?.class););
        });

        imgBtnNotification = findViewById(R.id.action_notification);
        imgBtnNotification.setOnClickListener(v -> {
            // startActivity(new Intent(this, Notification?.class););
        });

        imgBtnBigOwl = findViewById(R.id.action_big_owl);
        imgBtnBigOwl.setOnClickListener(v -> {
            // startActivity(new Intent(this, BigOwl?.class););
        });
    }

    protected abstract int getContentView();

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.overflow_home) {
            finish();
            startActivity(new Intent(this, HomePageActivity.class));
        } else if (item.getItemId() == R.id.overflow_refresh) {
            finish();
            startActivity(getIntent());
        } else if (item.getItemId() == R.id.overflow_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginPageActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}
