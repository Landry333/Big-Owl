package com.example.bigowlapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.bigowlapp.R;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BigOwlActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ImageButton imgBtnOverflow;
    ImageButton imgBtnSchedule;
    ImageButton imgBtnUser;
    ImageButton imgBtnNotification;
    ImageButton imgBtnBigOwl;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        progressBar = findViewById(R.id.database_progress_bar);

        imgBtnOverflow = findViewById(R.id.action_overflow);
        imgBtnOverflow.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.big_owl_overflow);
            popup.show();
        });

        imgBtnSchedule = findViewById(R.id.action_schedule);
        imgBtnSchedule.setOnClickListener(v -> {
            // TODO startActivity(new Intent(this, Schedule? .class));
        });

        imgBtnUser = findViewById(R.id.action_user);
        imgBtnUser.setOnClickListener(v -> {
            // TODO startActivity(new Intent(this, User? .class));
        });

        imgBtnNotification = findViewById(R.id.action_notification);
        imgBtnNotification.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));

        imgBtnBigOwl = findViewById(R.id.action_big_owl);
        imgBtnBigOwl.setOnClickListener(v -> {
            // TODO startActivity(new Intent(this, BigOwl? .class));
        });
    }

    protected void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void setProgressBarInvisible() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    protected abstract int getContentView();

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.overflow_home) {
            finish();
            startActivity(new Intent(this, HomePageActivity.class));
        } else if (item.getItemId() == R.id.overflow_wiki_page_manual) {
            final String WIKI_PAGE_MANUAL_URI = getString(R.string.wiki_page_manual_uri);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WIKI_PAGE_MANUAL_URI));
            startActivity(intent);
        } else if (item.getItemId() == R.id.overflow_refresh) {
            finish();
            startActivity(getIntent());
        } else if (item.getItemId() == R.id.overflow_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return false;
    }
}
