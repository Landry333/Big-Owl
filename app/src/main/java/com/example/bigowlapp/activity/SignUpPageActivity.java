package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.SignUpViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class SignUpPageActivity extends AppCompatActivity {
    public EditText userEmail, userPassword, userPhone, username;
    Button btnSignUp;
    TextView tvSignIn;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        initialize();
    }

    protected void initialize() {
        //Authentication with firebase
        username = findViewById(R.id.edit_text_text_person_name);
        userEmail = findViewById(R.id.edit_text_text_mail_address);
        userPassword = findViewById(R.id.edit_text_text_password);
        userPhone = findViewById(R.id.edit_text_phone);
        btnSignUp = findViewById(R.id.button_sign_up);
        tvSignIn = findViewById(R.id.text_view_sign_in);

        btnSignUp.setOnClickListener(v -> {
            String email = userEmail.getText().toString();
            String pass = userPassword.getText().toString();
            String userPhone = this.userPhone.getText().toString();
            String userName = username.getText().toString();

            //Error handling
            if (userName.isEmpty()) {
                username.setError("Please enter your name");
                username.requestFocus();
            } else if (email.isEmpty()) {
                userEmail.setError("Please enter a valid email");
                userEmail.requestFocus();
            } else if (pass.isEmpty()) {
                userPassword.setError("Please enter your password");
                userEmail.requestFocus();
            } else if (userPhone.isEmpty()) {
                this.userPhone.setError("please enter a phone number");
                this.userPhone.requestFocus();
            } else {
                signUpViewModel.createUser(email, pass, userPhone, userName)
                        .addOnSuccessListener(isSuccessful -> {
                            Toast.makeText(SignUpPageActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpPageActivity.this, HomePageActivity.class));
                        })
                        .addOnFailureListener(e -> Toast.makeText(SignUpPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpPageActivity.this, LoginPageActivity.class));
        });
    }
}