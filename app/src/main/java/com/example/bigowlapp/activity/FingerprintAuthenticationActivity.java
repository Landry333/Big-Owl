package com.example.bigowlapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.example.bigowlapp.viewModel.HomePageViewModel;
import com.google.i18n.phonenumbers.NumberParseException;

import java.util.concurrent.Executor;

public class FingerprintAuthenticationActivity extends AppCompatActivity {

    private TextView AuthResultText;
    private TextView fingerprintAuthRegistrationText;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;
    private Button btnStartFingerAuth;
    private Button btnLogOut;
    private Button btnFingerprintAuthRegistration;
    private HomePageViewModel homePageViewModel;
    private static final String PROPOSE_FINGERPRINT_AUTH = "Add fingerprint authentication to secure your account?\n\n" +
            "NOTE: This will also make your account accessible only by a device having sim card phone number same as your account phone number";
    private static final String IS_FINGERPRINT_AUTH_REGISTERED = "You are registered to fingerprint authentication.\n\n" +
            "You can modify your choice on Edit profile";
    private static final String NO_COMPATIBILITY_WITH_PHONE = "Sorry, this service is not available with this phone or " +
            "with your telephony provider\n\nPhone number on this phone should be the same as in your account";
    private static final String NOT_ALLOWED = "You are not allowed to access this account\n\n"+
            "Phone number on this phone should be the same as in your account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authenticaton);

        AuthResultText = findViewById(R.id.authentication_result_message);
        btnStartFingerAuth = findViewById(R.id.btn_start_authentication);
        btnFingerprintAuthRegistration = findViewById(R.id.fingerprint_auth_registration_btn);
        fingerprintAuthRegistrationText = findViewById(R.id.fingerprint_auth_registration_text);
        btnLogOut = findViewById(R.id.btn_logout);
        executor = ContextCompat.getMainExecutor(this);
        AuthResultText.setVisibility(View.GONE);
        btnStartFingerAuth.setVisibility(View.GONE);
        btnFingerprintAuthRegistration.setVisibility(View.GONE);

        //Toast.makeText(FingerprintAuthenticationActivity.this, "Authenticate fingerprint to get access", Toast.LENGTH_LONG).show();

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                AuthResultText.setText("Error");
                AuthResultText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //textView.setText("Success");
                //Toast.makeText(FingerprintAuthenticationActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                goToHomePage();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                AuthResultText.setText("Failed authentication");
                AuthResultText.setVisibility(View.VISIBLE);
            }
        });
        if (homePageViewModel == null) {
            homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        }

        btnStartFingerAuth.setOnClickListener(v -> {
            /*homePageViewModel.signOut();
            Intent intent = new Intent(HomePageActivity.this, LoginPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            fingerAuthenticate();
        });
        btnLogOut.setOnClickListener(v -> {
            homePageViewModel.signOut();
            Toast.makeText(FingerprintAuthenticationActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FingerprintAuthenticationActivity.this, LoginPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("BigOwl fingerprint Authentication")
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(false)
                .build();

    }

    @SuppressLint("MissingPermission")
    private void subscribeToData() {
        if (homePageViewModel.isCurrentUserSet()) {

            LiveDataWithStatus<User> currentUserData = homePageViewModel.getCurrentUserData();
            //setProgressBarVisible();
            currentUserData.observe(this, user -> {
                TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                String devicePhoneNumber = telephonyManager.getLine1Number();
                if (currentUserData.hasError()) {
                    Toast.makeText(getBaseContext(), currentUserData.getError().getMessage(), Toast.LENGTH_LONG).show();
                    // TODO: Handle this failure (exist page, modify page, or set up page for error case)
                    return;
                }
                String formattedDevicePhoneNum = null;
                try {
                    formattedDevicePhoneNum = PhoneNumberFormatter.formatNumber(devicePhoneNumber, this);
                } catch (NumberParseException e) {
                    Toast.makeText(this, "FAILED to format phone number. Process failed", Toast.LENGTH_LONG).show();
                }
                if (!user.getPhoneNumber().equalsIgnoreCase(formattedDevicePhoneNum) && !user.isFingerprintAuthRegistration()) {
                    Log.e("user.isFingerprintAuthRegistration()", ""+user.isFingerprintAuthRegistration());
                    fingerprintAuthRegistrationText.setText(NO_COMPATIBILITY_WITH_PHONE);
                    btnFingerprintAuthRegistration.setText("Go to home page");
                    btnFingerprintAuthRegistration.setVisibility(View.VISIBLE);
                    btnFingerprintAuthRegistration.setOnClickListener(v -> goToHomePage());
                    return;
                }
                if (!user.isFingerprintAuthRegistration()) {
                    fingerprintAuthRegistrationText.setText(PROPOSE_FINGERPRINT_AUTH);
                    btnFingerprintAuthRegistration.setText("ADD");
                    btnFingerprintAuthRegistration.setVisibility(View.VISIBLE);
                    btnFingerprintAuthRegistration.setOnClickListener(v -> {
                        user.setFingerprintAuthRegistration(true);
                        Log.e("user last name", user.getLastName());
                        fingerprintAuthRegistrationText.setText(IS_FINGERPRINT_AUTH_REGISTERED);
                        btnFingerprintAuthRegistration.setVisibility(View.GONE);
                        btnStartFingerAuth.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "You now have the fingerprint authentication", Toast.LENGTH_LONG).show();
                    });
                } else {
                    if (user.getPhoneNumber().equalsIgnoreCase(formattedDevicePhoneNum)) {
                        fingerprintAuthRegistrationText.setText(IS_FINGERPRINT_AUTH_REGISTERED);
                        btnFingerprintAuthRegistration.setVisibility(View.GONE);
                        btnStartFingerAuth.setVisibility(View.VISIBLE);
                    }
                    else{
                        fingerprintAuthRegistrationText.setText(NOT_ALLOWED);
                    }
                }

            });
        }
    }

    @SuppressLint({"MissingPermission"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        if (homePageViewModel == null) {
            homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        }
        subscribeToData();
    }

    public void fingerAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {

            AuthResultText.setText("Biometric Not Supported");
            return;
        }
        biometricPrompt.authenticate(promptInfo);
    }

    public void goToHomePage() {
        Intent i = new Intent(FingerprintAuthenticationActivity.this, HomePageActivity.class);
        startActivity(i);
    }
}