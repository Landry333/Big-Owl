package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.HomePageViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomePageActivity extends BigOwlActivity {
    private final int LOCATION_PERMISSION_CODE = 1;

    private Button btnLogOut;
    private Button btnAddUsers;
    private Button btnMonitoringGroup;
    private Button btnSupervisedGroup;
    private Button btnSetSchedule;
    private ScrollView scrollView;
    private ImageView imgUserAvatar;
    private TextView textEmail;
    private TextView textFirstName;
    private TextView textLastName;
    private TextView textPhone;
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
            homePageViewModel.signOut();
            Intent intent = new Intent(HomePageActivity.this, LoginPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnSetSchedule = findViewById(R.id.btn_set_schedule);

        btnSetSchedule.setOnClickListener(v -> {
            Intent i = new Intent(HomePageActivity.this, SetScheduleActivity.class);
            startActivity(i);
        });

        btnAddUsers = findViewById(R.id.btn_add_users);

        btnAddUsers.setOnClickListener(v -> {
            Intent i = new Intent(HomePageActivity.this, AddUsersActivity.class);
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

        imgBtnOverflow = findViewById(R.id.action_overflow);
        imgBtnOverflow.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.big_owl_overflow);
            for (int i = 0; i < popup.getMenu().size(); i++) {
                if (popup.getMenu().getItem(i).getItemId() == R.id.overflow_refresh) {
                    popup.getMenu().add(Menu.NONE, View.generateViewId(), i + 1, "Edit Profile");
                    break;
                }
            }
            popup.show();
        });


        // TODO: cleanup permissions request code

        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            permissionsToAskFor.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            permissionsToAskFor.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionsToAskFor.isEmpty()) {
            requestLocationPermission(permissionsToAskFor);
        }
    }

    private void subscribeToData() {
        if (homePageViewModel.isCurrentUserSet()) {
            imgUserAvatar = findViewById(R.id.user_avatar);
            textEmail = findViewById(R.id.user_email);
            textFirstName = findViewById(R.id.user_first_name);
            textLastName = findViewById(R.id.user_last_name);
            textPhone = findViewById(R.id.user_phone_number);

            LiveDataWithStatus<User> currentUserData = homePageViewModel.getCurrentUserData();
            currentUserData.observe(this, user -> {
                if (currentUserData.hasError()) {
                    Toast.makeText(getBaseContext(), currentUserData.getError().getMessage(), Toast.LENGTH_LONG).show();
                    // TODO: Handle this failure (exist page, modify page, or set up page for error case)
                    return;
                }

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
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getTitle().equals("Edit Profile")) {
            finish();
            startActivity(new Intent(this, EditProfileActivity.class));
        }
        return super.onMenuItemClick(item);
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

    // TODO: Might be better to request these permission when the user accepts a schedule
    private void requestLocationPermission(ArrayList<String> permissionsToAskFor) {

        Runnable permissionRequest = () ->
                ActivityCompat.requestPermissions(this, permissionsToAskFor.toArray(new String[0]), LOCATION_PERMISSION_CODE);

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location permission needed")
                    .setMessage("Location detection is used to check whether you have arrived to a scheduled location")
                    .setPositiveButton("Ok", (dialogInterface, which) -> permissionRequest.run())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            permissionRequest.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            handleLocationPermissionResult(grantResults);
        }
    }

    private void handleLocationPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
        }
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