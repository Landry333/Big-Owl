package com.example.bigowlapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.MemberScheduleAlarmManager;
import com.example.bigowlapp.viewModel.HomePageViewModel;
import com.squareup.picasso.Picasso;


public class HomePageActivity extends BigOwlActivity {

    private Button btnLogOut;
    private Button btnAddUsers;
    private Button btnMonitoringGroup;
    private Button btnSupervisedGroup;
    private ScrollView scrollView;
    private ImageView imgUserAvatar;
    private TextView textEmail;
    private TextView textFirstName;
    private TextView textLastName;
    private TextView textPhone;
    private HomePageViewModel homePageViewModel;
    private MemberScheduleAlarmManager memberScheduleAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 10);
        }
        // RECEIVE and SEND SMS permissions for the text sms authentication system
        else if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
        }
        else if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 100);
        }
        else if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, 100);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS READ Permission GRANTED", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Need to allow SMS permission or reinstall app then allow", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS SEND Permission GRANTED", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Need to allow SMS permission or reinstall app then allow", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PHONE Permission GRANTED", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Need to allow PHONE permission or reinstall app then allow", Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

    @SuppressLint({"MissingPermission"})
    @RequiresApi(api = Build.VERSION_CODES.O)
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
    }

    private void subscribeToData() {
        if (homePageViewModel.isCurrentUserSet()) {
            imgUserAvatar = findViewById(R.id.user_avatar);
            textEmail = findViewById(R.id.user_email);
            textFirstName = findViewById(R.id.user_first_name);
            textLastName = findViewById(R.id.user_last_name);
            textPhone = findViewById(R.id.user_phone_number);

            LiveDataWithStatus<User> currentUserData = homePageViewModel.getCurrentUserData();
            setProgressBarVisible();
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
                setProgressBarInvisible();
            });
            scrollView.setVisibility(View.VISIBLE);
            initAlarmManager();
        }
        /*  TODO: find a way to uncomment out below lines and allow HomePageActivityTest to pass
        else {
            this.noSignedInAlert().show();
        }
         */
    }

    /**
     * Initializes the AlarmManager to set alarms for the user's schedules that he has to attend to.
     */
    private void initAlarmManager() {
        if (memberScheduleAlarmManager == null) {
            memberScheduleAlarmManager = new MemberScheduleAlarmManager(this);
        }
        memberScheduleAlarmManager.setAlarms(homePageViewModel.getCurrentUserUid());
    }

    @Override
    public int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getTitle().equals("Edit Profile")) {
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

    @VisibleForTesting
    public HomePageViewModel getHomePageViewModel() {
        return homePageViewModel;
    }

    @VisibleForTesting
    public void setHomePageViewModel(HomePageViewModel homePageViewModel) {
        this.homePageViewModel = homePageViewModel;
    }

    @VisibleForTesting
    public void setMemberScheduleAlarmManager(MemberScheduleAlarmManager memberScheduleAlarmManager) {
        this.memberScheduleAlarmManager = memberScheduleAlarmManager;
    }
}