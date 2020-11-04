package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import com.example.bigowlapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {
    Button edit_buttonCancel, edit_buttonConfirm;
    EditText edit_userFirstName, edit_userLastName, edit_phoneNumber, edit_imageURL;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
    }

    protected void initialize() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        edit_buttonCancel = findViewById(R.id.edit_buttonCancel);
        edit_buttonConfirm = findViewById(R.id.edit_buttonConfirm);
        edit_userFirstName = findViewById(R.id.edit_userFirstName);
        edit_userLastName = findViewById(R.id.edit_userLastName);
        edit_phoneNumber = findViewById(R.id.edit_phoneNumber);
        edit_imageURL = findViewById(R.id.edit_imageURL);

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
            } else if (edit_imageURL.getText().toString().isEmpty() || URLUtil.isValidUrl(edit_imageURL.getText().toString())) {
                edit_imageURL.setError("Please enter a valid image URL.");
                edit_imageURL.requestFocus();
            } else {
                FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid())
                        .update(
                                "firstName", edit_userFirstName.getText().toString(),
                                "lastName", edit_userLastName.getText().toString(),
                                "phoneNumber", edit_phoneNumber.getText().toString(),
                                "profileImage", edit_imageURL.getText().toString());
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