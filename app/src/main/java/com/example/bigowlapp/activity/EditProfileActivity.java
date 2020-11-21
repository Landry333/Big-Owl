package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.EditProfileViewModel;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

public class EditProfileActivity extends BigOwlActivity {
    Button editButtonCancel, editButtonConfirm;
    EditText editUserFirstName, editUserLastName, editPhoneNumber, editImageURL;
    private EditProfileViewModel editProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (editProfileViewModel == null) {
            editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        }
        subscribeToData();
    }

    protected void initialize() {
        editButtonConfirm = findViewById(R.id.edit_button_confirm);
        editButtonConfirm.setOnClickListener(v -> {

            if (editUserFirstName.getText().toString().isEmpty()) {
                editUserFirstName.setError("Please enter a valid first name.");
                editUserFirstName.requestFocus();
            }
            if (editUserLastName.getText().toString().isEmpty()) {
                editUserLastName.setError("Please enter a valid last name.");
                editUserLastName.requestFocus();
            }
            if (editPhoneNumber.getText().toString().isEmpty()) {
                editPhoneNumber.setError("Please enter a valid phone number.");
                editPhoneNumber.requestFocus();
            }
            if (!editUserFirstName.getText().toString().isEmpty() &&
                    !editUserLastName.getText().toString().isEmpty() &&
                    !editPhoneNumber.getText().toString().isEmpty()) {
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

        editButtonCancel = findViewById(R.id.edit_button_cancel);
        editButtonCancel.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
            finish();
        });
    }

    private void subscribeToData() {
        if (editProfileViewModel.isCurrentUserSet()) {
            editUserFirstName = findViewById(R.id.edit_user_first_name);
            editUserLastName = findViewById(R.id.edit_user_last_name);
            editPhoneNumber = findViewById(R.id.edit_user_phone_number);
            editImageURL = findViewById(R.id.edit_user_image_url);

            editProfileViewModel.getCurrentUserData().observe(this, user -> {
                editUserFirstName.setText(user.getFirstName());
                editUserLastName.setText(user.getLastName());
                editPhoneNumber.setText(user.getPhoneNumber());
                editImageURL.setText(user.getProfileImage());
            });
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected String getToolbarTitle() {
        return "Edit Profile";
    }

    @VisibleForTesting
    public void setHomePageViewModel(EditProfileViewModel editProfileViewModel) {
        this.editProfileViewModel = editProfileViewModel;
    }

}