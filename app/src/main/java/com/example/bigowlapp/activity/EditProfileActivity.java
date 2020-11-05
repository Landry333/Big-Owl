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
    Button edit_buttonCancel, edit_buttonConfirm;
    EditText edit_userFirstName, edit_userLastName, edit_phoneNumber, edit_imageURL;
    private EditProfileViewModel editProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
    }

    protected void initialize() {
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        edit_buttonCancel = findViewById(R.id.edit_buttonCancel);
        edit_buttonConfirm = findViewById(R.id.edit_buttonConfirm);
        edit_userFirstName = findViewById(R.id.edit_userFirstName);
        edit_userLastName = findViewById(R.id.edit_userLastName);
        edit_phoneNumber = findViewById(R.id.edit_phoneNumber);
        edit_imageURL = findViewById(R.id.edit_imageURL);

        editProfileViewModel.getCurrentUserProfile().observe(this, user -> {
            edit_userFirstName.setText(user.getFirstName());
            edit_userLastName.setText(user.getLastName());
            edit_phoneNumber.setText(user.getPhoneNumber());
            edit_imageURL.setText(user.getProfileImage());
        });

        edit_buttonConfirm.setOnClickListener(v -> {
            if (edit_userFirstName.getText().toString().isEmpty()) {
                edit_userFirstName.setError("Please enter a valid first name.");
                edit_userFirstName.requestFocus();
            } else if (edit_userLastName.getText().toString().isEmpty()) {
                edit_userLastName.setError("Please enter a valid last name.");
                edit_userLastName.requestFocus();
            } else if (edit_phoneNumber.getText().toString().isEmpty()) {
                edit_phoneNumber.setError("Please enter a valid phone number.");
                edit_phoneNumber.requestFocus();
            }
            /* allow user to not have profile image for now
            else if (edit_imageURL.getText().toString().isEmpty() || URLUtil.isValidUrl(edit_imageURL.getText().toString())) {
                edit_imageURL.setError("Please enter a valid image URL.");
                edit_imageURL.requestFocus();
            }
            */
            else {
                editProfileViewModel.editUserProfile(
                        edit_userFirstName.getText().toString(),
                        edit_userLastName.getText().toString(),
                        edit_phoneNumber.getText().toString(),
                        edit_imageURL.getText().toString()
                );

                startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
                finish();
            }
        });

        edit_buttonCancel.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
            finish();
        });
    }

}