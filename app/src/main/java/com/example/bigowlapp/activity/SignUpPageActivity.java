package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.SignUpViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpPageActivity extends AppCompatActivity {
    public EditText emailId, password, phone, name;
    Button btnSignUp;
    TextView tvSignIn;
    private User user = new User();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        initialize();
    }

    protected void initialize() {
        try {
            //Authentication with firebase
            emailId = findViewById(R.id.editTextTextEmailAddress);
            password = findViewById(R.id.editTextTextPassword);
            phone = findViewById(R.id.editTextPhone);
            name = findViewById(R.id.editTextTextPersonName);
            btnSignUp = findViewById(R.id.button);
            tvSignIn = findViewById(R.id.textView);

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailId.getText().toString();
                    String pass = password.getText().toString();
                    String userPhone = phone.getText().toString();
                    String userName = name.getText().toString();

                    //Error handling
                    if (email.isEmpty()) {
                        emailId.setError("Please enter a valid email");
                        emailId.requestFocus();
                    } else if (pass.isEmpty()) {
                        password.setError("Please enter your password");
                        emailId.requestFocus();
                    } else if (userPhone.isEmpty()) {
                        phone.setError("please enter a phone number");
                        phone.requestFocus();
                    } else if (email.isEmpty() || pass.isEmpty() || userPhone.isEmpty()) {
                        Toast.makeText(SignUpPageActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                    } else if (!(email.isEmpty() && pass.isEmpty() && userPhone.isEmpty())) {
                        signUpViewModel.createUser(email, pass, userPhone, userName)
                                .addOnSuccessListener(isSuccessful -> {
                                    Toast.makeText(SignUpPageActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpPageActivity.this, HomePageActivity.class));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUpPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(SignUpPageActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SignUpPageActivity.this, LoginPageActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {

        }
    }
}