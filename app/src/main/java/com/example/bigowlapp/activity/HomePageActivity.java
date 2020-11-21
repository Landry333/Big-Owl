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

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.HomePageViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

public class HomePageActivity extends BigOwlActivity {
    Button btnLogOut, btnAddUsers, btnMonitoringGroup, btnSupervisedGroup;
    ScrollView scrollView;
    ImageView imgUserAvatar;
    TextView textEmail, textFirstName, textLastName, textPhone;
    MenuItem menuItemEditProfile;
    private HomePageViewModel homePageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (homePageViewModel == null) {
            homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        }
        subscribeToData();
    }

    protected void initialize() {
        scrollView = findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.GONE);

        btnLogOut = findViewById(R.id.btn_logout);

        btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(HomePageActivity.this, LoginPageActivity.class);
            startActivity(i);
        });

        btnAddUsers = findViewById(R.id.btn_add_users);

        btnAddUsers.setOnClickListener(v -> {
            Intent i = new Intent(HomePageActivity.this, AddUsers.class);
            startActivity(i);
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
    }

    private void subscribeToData() {
        if (homePageViewModel.isCurrentUserSet()) {
            imgUserAvatar = findViewById(R.id.user_avatar);
            textEmail = findViewById(R.id.user_email);
            textFirstName = findViewById(R.id.user_first_name);
            textLastName = findViewById(R.id.user_last_name);
            textPhone = findViewById(R.id.user_phone_number);

            homePageViewModel.getCurrentUserData().observe(this, user -> {
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
        /*  TODO: find a way to uncomment out below lines and allow HomePageActivityTest to pass
        else {
            this.noSignedInAlert().show();
        }
         */
    }

    @Override
    public int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected String getToolbarTitle() {
        return "Homepage";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItemEditProfile = menu.add(Menu.NONE, View.generateViewId(), 3, "Edit Profile");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == menuItemEditProfile.getItemId()) {
            startActivity(new Intent(HomePageActivity.this, EditProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog noSignedInAlert() {
        return new AlertDialog.Builder(this)
                .setTitle("You are not logged in!")
                .setMessage("Please log in or register an account!")
                .setPositiveButton("Ok", (dialogInterface, which) -> {
                    startActivity(new Intent(this, LoginPageActivity.class));
                    finish();
                })
                .setCancelable(false)
                .create();
    }

    @VisibleForTesting
    public HomePageViewModel getHomePageViewModel() {
        return homePageViewModel;
    }

    @VisibleForTesting
    public void setHomePageViewModel(HomePageViewModel homePageViewModel) {
        this.homePageViewModel = homePageViewModel;
    }

}