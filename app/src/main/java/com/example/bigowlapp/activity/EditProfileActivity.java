package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.EditProfileViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class EditProfileActivity extends AppCompatActivity {
    Button editButtonCancel, editButtonConfirm;
    EditText editUserFirstName, editUserLastName, editPhoneNumber, editImageURL;
    private EditProfileViewModel editProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
    }

    protected void initialize() {
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        editButtonCancel = findViewById(R.id.editButtonCancel);
        editButtonConfirm = findViewById(R.id.editButtonConfirm);
        editUserFirstName = findViewById(R.id.editUserFirstName);
        editUserLastName = findViewById(R.id.editUserLastName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editImageURL = findViewById(R.id.editImageURL);

        editProfileViewModel.getCurrentUserProfile().observe(this, user -> {
            editUserFirstName.setText(user.getFirstName());
            editUserLastName.setText(user.getLastName());
            editPhoneNumber.setText(user.getPhoneNumber());
            editImageURL.setText(user.getProfileImage());
        });

        editButtonConfirm.setOnClickListener(v -> {
            if (editUserFirstName.getText().toString().isEmpty()) {
                editUserFirstName.setError("Please enter a valid first name.");
                editUserFirstName.requestFocus();
            } else if (editUserLastName.getText().toString().isEmpty()) {
                editUserLastName.setError("Please enter a valid last name.");
                editUserLastName.requestFocus();
            } else if (editPhoneNumber.getText().toString().isEmpty()) {
                editPhoneNumber.setError("Please enter a valid phone number.");
                editPhoneNumber.requestFocus();
            }
            else {
                editProfileViewModel.editUserProfile(
                        editUserFirstName.getText().toString(),
                        editUserLastName.getText().toString(),
                        editPhoneNumber.getText().toString(),
                        editImageURL.getText().toString()
                );

                startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
                finish();
            }
        });

        editButtonCancel.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
            finish();
        });
    }

}