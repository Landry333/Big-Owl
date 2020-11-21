package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bigowlapp.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BigOwlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getToolbarTitle());
        setSupportActionBar(toolbar);
    }

    protected abstract int getContentView();

    protected String getToolbarTitle() {
        return "Big Owl App";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.big_owl_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.overflow_home) {
            finish();
            startActivity(getIntent());
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
