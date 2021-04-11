package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.NotificationListenerManager;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.example.bigowlapp.view_model.LogInViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPageActivity extends AppCompatActivity {
    private EditText emailId;
    private EditText password;
    private Button btnSignIn;
    private TextView tvSignUp;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private LogInViewModel logInViewModel;
    private NotificationListenerManager notificationListener;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (logInViewModel == null) {
            logInViewModel = new ViewModelProvider(this).get(LogInViewModel.class);
        }
        initialize();
    }

    protected void initialize() {
        progressBar = findViewById(R.id.login_progress_bar);

        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btnSignIn = findViewById(R.id.button);
        tvSignUp = findViewById(R.id.textView);

        mAuthStateListener = firebaseAuth -> {
            if (logInViewModel.isCurrentUserSet()) {
                Toast.makeText(LoginPageActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                notificationListener = new NotificationListenerManager(this);
                notificationListener.listen();
                checkNextAccessWhenIsLoggedIn();
            } else {
                Toast.makeText(LoginPageActivity.this, "Please login", Toast.LENGTH_SHORT).show();
            }
        };

        btnSignIn.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String pass = password.getText().toString();
            if (email.isEmpty()) {
                emailId.setError("Please enter a valid email");
                emailId.requestFocus();
            } else if (pass.isEmpty()) {
                password.setError("Please enter your password");
                password.requestFocus();
            } else if (!(email.isEmpty() && pass.isEmpty())) {
                progressBar.setVisibility(View.VISIBLE);
                logInViewModel.logInUser(email, pass)
                        .addOnSuccessListener(isSuccessful -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            checkIfCanFingerprintAuthenticate();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginPageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        });
            } else {
                Toast.makeText(LoginPageActivity.this, "An error has occurred", Toast.LENGTH_LONG).show();
            }
        });

        tvSignUp.setOnClickListener(v -> {
            Intent i = new Intent(LoginPageActivity.this, SignUpPageActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
            logInViewModel.addAuthStateListenerToDatabase(mAuthStateListener);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    public void checkIfCanFingerprintAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        Intent intent;
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "you are logged in!", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, HomePageActivity.class);
        } else {
            Toast.makeText(this, "user ID and password accepted", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, FingerprintAuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);
    }

    public void checkNextAccessWhenIsLoggedIn() {
        LiveDataWithStatus<User> currentUserData = logInViewModel.getCurrentUserData();
        currentUserData.observe(this, user -> {
            if (currentUserData.hasError()) {
                Toast.makeText(getBaseContext(), currentUserData.getError().getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent;
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
                intent = new Intent(this, HomePageActivity.class);
            } else {
                PhoneNumberFormatter phoneNumberFormatter = new PhoneNumberFormatter(this);
                String formattedDevicePhoneNum = phoneNumberFormatter.getFormattedSMSNumber();
                if (user.getFingerprintAuthRegistration().equalsIgnoreCase("YES") && !user.getPhoneNumber().equalsIgnoreCase(formattedDevicePhoneNum)) {
                    intent = new Intent(this, FingerprintAuthenticationActivity.class);
                } else {
                    intent = new Intent(this, HomePageActivity.class);
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @VisibleForTesting
    public void setLogInViewModel(LogInViewModel logInViewModel) {
        this.logInViewModel = logInViewModel;
    }
}
