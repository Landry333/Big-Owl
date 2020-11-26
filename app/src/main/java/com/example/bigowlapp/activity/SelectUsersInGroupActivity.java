package com.example.bigowlapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.example.bigowlapp.fragments.UserFragment;

public class SelectUsersInGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users_in_group);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.user_list_container, UserFragment.newInstance(1))
                    .commitNow();
        }
    }
}