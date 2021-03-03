package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.repository.exception.EmptyFieldException;
import com.example.bigowlapp.viewModel.EditProfileViewModel;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Objects;

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
            onClickConfirmButton();
        });

        editButtonCancel = findViewById(R.id.edit_button_cancel);
        editButtonCancel.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
            finish();
        });
    }

    private void onClickConfirmButton() {
        String userPhone = editPhoneNumber.getText().toString();
        String firstName = editUserFirstName.getText().toString();
        String lastName = editUserLastName.getText().toString();

        if (firstName.isEmpty()) {
            editUserFirstName.setError("Please enter a valid first name.");
            editUserFirstName.requestFocus();
        }
        if (lastName.isEmpty()) {
            editUserLastName.setError("Please enter a valid last name.");
            editUserLastName.requestFocus();
        }

        String formattedPhone = phoneNumberFormatter(userPhone);

        if (!firstName.isEmpty() && !lastName.isEmpty() && formattedPhone != null) {
            editProfileViewModel.isPhoneNumberTaken(formattedPhone)
                    .addOnSuccessListener(isSuccessful -> {
                        editProfileViewModel.editUserProfile(
                                firstName,
                                editUserLastName.getText().toString(),
                                formattedPhone,
                                editImageURL.getText().toString()
                        );
                        startActivity(new Intent(EditProfileActivity.this, HomePageActivity.class));
                        finish();
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String phoneNumberFormatter(String userPhone) {
        String formattedPhone = null;
        try {
            formattedPhone = filteredNUmber(userPhone);
        } catch (NumberParseException | EmptyFieldException e) {
            editPhoneNumber.setError(e.getMessage());
            editPhoneNumber.requestFocus();
        }
        return formattedPhone;
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

    @VisibleForTesting
    public void setHomePageViewModel(EditProfileViewModel editProfileViewModel) {
        this.editProfileViewModel = editProfileViewModel;
    }

    public String filteredNUmber(String number) throws NumberParseException, EmptyFieldException {
        if (number == null || number.isEmpty()) {
            throw new EmptyFieldException("Please enter a valid phone number.");
        }
        PhoneNumberUtil numbUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phonenumber = numbUtil.parseAndKeepRawInput(number, getResources().getConfiguration().getLocales().get(0).getCountry());
        return numbUtil.format(phonenumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

}