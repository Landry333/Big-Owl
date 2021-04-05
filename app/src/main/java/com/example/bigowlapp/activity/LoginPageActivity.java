package com.example.bigowlapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import com.example.bigowlapp.utils.NotificationListenerManager;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.example.bigowlapp.viewModel.HomePageViewModel;
import com.example.bigowlapp.viewModel.LogInViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.i18n.phonenumbers.NumberParseException;

public class LoginPageActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private LogInViewModel logInViewModel;
    private NotificationListenerManager noficationListener;
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

        try {
            progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

            emailId = findViewById(R.id.editTextTextEmailAddress);
            password = findViewById(R.id.editTextTextPassword);
            btnSignIn = findViewById(R.id.button);
            tvSignUp = findViewById(R.id.textView);

            mAuthStateListener = firebaseAuth -> {
                if (logInViewModel.isCurrentUserSet()) {
                    Toast.makeText(LoginPageActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginPageActivity.this, HomePageActivity.class);
                    noficationListener = new NotificationListenerManager();
                    noficationListener.listen(this);
                    checkNextAccessWhenIsLoggedIn();
                } else {
                    Toast.makeText(LoginPageActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }
            };

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });

            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginPageActivity.this, SignUpPageActivity.class);
                    startActivity(i);
                }
            });
        } catch (Exception ex) {

        }
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

    @SuppressLint("MissingPermission")
// Permission was already provided by user before sign in step in order to proceed
    public void checkNextAccessWhenIsLoggedIn() {
        HomePageViewModel homePageViewModel;
        homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        LiveDataWithStatus<User> currentUserData = homePageViewModel.getCurrentUserData();
        currentUserData.observe(this, user -> {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String devicePhoneNumber = telephonyManager.getLine1Number();
            if (currentUserData.hasError()) {
                Toast.makeText(getBaseContext(), currentUserData.getError().getMessage(), Toast.LENGTH_LONG).show();
                // TODO: Handle this failure (exist page, modify page, or set up page for error case).Same TODO from HomepageActivity
                return;
            }
            String formattedDevicePhoneNum = null;
            try {
                formattedDevicePhoneNum = new PhoneNumberFormatter(this).formatNumber(devicePhoneNumber);
            } catch (NumberParseException e) {
                Toast.makeText(this, "FAILED to format phone number. Process failed", Toast.LENGTH_LONG).show();
            }
            Intent intent;
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
                intent = new Intent(this, HomePageActivity.class);
            } else {
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
