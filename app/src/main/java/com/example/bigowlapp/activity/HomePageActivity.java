package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class HomePageActivity extends AppCompatActivity {
    private final int CONTACT_PERMISSION_CODE = 1;
    Button btnLogOut, sendSmsInvitation, btnSearchUsers, btnMonitoringGroup, btnSupervisedGroup;
    ScrollView scrollView;
    ImageView imgUserAvatar;
    TextView textEmail, textFirstName, textLastName, textPhone;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initialize() {
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference docRef = db.collection("users").document(currentUserUid);
                docRef.get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            imgUserAvatar = findViewById(R.id.imageView_userAvatar);
                            textEmail = findViewById(R.id.textView_email);
                            textFirstName = findViewById(R.id.textView_firstName);
                            textLastName = findViewById(R.id.textView_lastName);
                            textPhone = findViewById(R.id.textView_phoneNumber);

                            if (URLUtil.isValidUrl((String) document.getString("profileImage")))
                                Picasso.get().load((String) document.getString("profileImage")).into(imgUserAvatar);
                            else imgUserAvatar.setImageResource(R.drawable.logo_square);

                            textEmail.setText((String) document.getString("email"));
                            textFirstName.setText((String) document.getString("firstName"));
                            textLastName.setText((String) document.getString("lastName"));
                            textPhone.setText((String) document.getString("phoneNumber"));
                        }
                    }
                    scrollView.setVisibility(View.VISIBLE);
                });
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
                    //Toast.makeText(HomePageActivity.this, "This permission is already granted", Toast.LENGTH_SHORT).show();
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