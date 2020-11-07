package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class HomePageActivity extends AppCompatActivity {
    Button btnLogOut, btnAddUsers, btnMonitoringGroup, btnSupervisedGroup;
    ScrollView scrollView;
    ImageView imgUserAvatar;
    TextView textEmail, textFirstName, textLastName, textPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
    }

    protected void initialize() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                LiveData<User> userData = new UserRepository().getDocumentByUId(
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        User.class);

                imgUserAvatar = findViewById(R.id.user_avatar);
                textEmail = findViewById(R.id.user_email);
                textFirstName = findViewById(R.id.user_first_name);
                textLastName = findViewById(R.id.user_last_name);
                textPhone = findViewById(R.id.user_phone_number);

                userData.observe(this, user -> {
                    textEmail.setText(user.getEmail());
                    textFirstName.setText(user.getFirstName());
                    textLastName.setText(user.getLastName());
                    textPhone.setText(user.getPhoneNumber());

                    Picasso.get()
                            .load(user.getProfileImage() == null || user.getProfileImage().isEmpty() ?
                                    null : user.getProfileImage())
                            .placeholder(R.drawable.logo_square)
                            .error(R.drawable.logo_square)
                            .into(imgUserAvatar);
                });

                scrollView.setVisibility(View.VISIBLE);
            }

            btnLogOut = findViewById(R.id.btn_logout);

            btnLogOut.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomePageActivity.this, LoginPageActivity.class);
                startActivity(i);
            });

            btnAddUsers = findViewById(R.id.btnAddUsers);

            btnAddUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(HomePageActivity.this, AddUsers.class);
                    startActivity(i);
                }
            });

            btnMonitoringGroup = findViewById(R.id.btn_monitoring_group);

            btnMonitoringGroup.setOnClickListener(v -> {
                Intent i = new Intent(HomePageActivity.this, MonitoringGroupPageActivity.class);
                startActivity(i);
            });

            btnSupervisedGroup = findViewById(R.id.btn_supervised_group);

            btnSupervisedGroup.setOnClickListener(v -> {
                Intent i = new Intent(HomePageActivity.this, SupervisedGroupListActivity.class);
                startActivity(i);
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_overflow, menu);
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
        } else if (item.getItemId() == R.id.overflow_edit_profile) {
            Intent i = new Intent(HomePageActivity.this, EditProfileActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}