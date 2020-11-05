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

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

public class HomePageActivity extends AppCompatActivity {
    private final int CONTACT_PERMISSION_CODE = 1;
    Button btnLogOut, sendSmsInvitation, btnSearchUsers, btnMonitoringGroup, btnSupervisedGroup;
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

                imgUserAvatar = findViewById(R.id.userAvatar);
                textEmail = findViewById(R.id.userEmail);
                textFirstName = findViewById(R.id.userFirstName);
                textLastName = findViewById(R.id.userLastName);
                textPhone = findViewById(R.id.userPhoneNumber);

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

            btnLogOut = findViewById(R.id.Logout);

            btnLogOut.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomePageActivity.this, LoginPageActivity.class);
                startActivity(i);
            });

            btnSearchUsers = findViewById(R.id.btnPhoneContacts);

            btnSearchUsers.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(HomePageActivity.this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(HomePageActivity.this, SearchContactsToSupervise.class);
                    startActivity(i);
                } else {
                    requestContactPermission();
                }
            });

            sendSmsInvitation = findViewById(R.id.SendSmsInvitation);

            sendSmsInvitation.setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, SendSmsInvitationActivity.class);
                startActivity(intent);
                finish();
            });

            btnMonitoringGroup = findViewById(R.id.btnMonitoringGroup);

            btnMonitoringGroup.setOnClickListener(v -> {
                Intent i = new Intent(HomePageActivity.this, MonitoringGroupPageActivity.class);
                startActivity(i);
            });

            btnSupervisedGroup = findViewById(R.id.btnSupervisedGroup);

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
        if (item.getItemId() == R.id.overflowHome) {
            finish();
            startActivity(getIntent());
        } else if (item.getItemId() == R.id.overflowRefresh) {
            finish();
            startActivity(getIntent());
        } else if (item.getItemId() == R.id.overflowEditProfile) {
            Intent i = new Intent(HomePageActivity.this, EditProfileActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO refactor to another class file
    private void requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission is required to read phone Contacts")
                    .setPositiveButton("Ok",
                            (dialogInterface, which) -> ActivityCompat.requestPermissions(
                                    HomePageActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    CONTACT_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(HomePageActivity.this, SearchContactsToSupervise.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}