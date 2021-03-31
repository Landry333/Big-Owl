package com.example.bigowlapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private Button btnStartFingerAuth;
    private Button btnGoToHomePage;
    private Button btnFingerprintAuthAdd;
    private Button btnFingerprintAuthMaybeLater;
    private HomePageViewModel homePageViewModel;
    private static final String PROPOSE_FINGERPRINT_AUTH = "Add fingerprint authentication to secure your account?\n\n" +
            "NOTE: This will also make your account ONLY accessible by a device having sim card_1 phone number same " +
            "as the phone number you saved in your account";
    private static final String IS_FINGERPRINT_AUTH_REGISTERED = "You are registered to fingerprint authentication.\n\n" +
            "You can modify your choice on Edit profile";
    private static final String NO_COMPATIBILITY_WITH_PHONE = "Sorry, this additional security service is not available on" +
            " this number or with your phone and telephony provider\n\n" +
            " First make sure sim card_1 phone number on this phone is the same as in your account";
    private static final String NOT_ALLOWED = "You are not allowed to access this account\n\n" +
            "Sim card_1 phone number on this phone should be the same as in your account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authenticaton);

        AuthResultText = findViewById(R.id.authentication_result_message);
        fingerprintAuthRegistrationText = findViewById(R.id.fingerprint_auth_registration_text);
        btnStartFingerAuth = findViewById(R.id.btn_start_authentication);
        btnFingerprintAuthAdd = findViewById(R.id.fingerprint_auth_add_btn);
        btnFingerprintAuthMaybeLater = findViewById(R.id.fingerprint_auth_maybe_later_btn);
        btnGoToHomePage = findViewById(R.id.btn_go_to_home_page);
        Button btnLogOut = findViewById(R.id.btn_logout);
        Executor executor = ContextCompat.getMainExecutor(this);
        AuthResultText.setVisibility(View.GONE);
        btnStartFingerAuth.setVisibility(View.GONE);
        btnGoToHomePage.setVisibility(View.GONE);
        btnFingerprintAuthAdd.setVisibility(View.GONE);
        btnFingerprintAuthMaybeLater.setVisibility(View.GONE);

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
                Toast.makeText(FingerprintAuthenticationActivity.this, "You are logged in", Toast.LENGTH_LONG).show();
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

        btnStartFingerAuth.setOnClickListener(v -> fingerAuthenticate());
        btnLogOut.setOnClickListener(v -> {
            homePageViewModel.signOut();
            Toast.makeText(FingerprintAuthenticationActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FingerprintAuthenticationActivity.this, LoginPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        btnFingerprintAuthMaybeLater.setOnClickListener(v -> {
            goToHomePage();
            Toast.makeText(this, "You are logged in. Edit profile to change your choice", Toast.LENGTH_LONG).show();
        });
        btnGoToHomePage.setOnClickListener(v -> goToHomePage());

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("BigOwl fingerprint Authentication")
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(false)
                .build();

    }

    @SuppressLint("MissingPermission")
// Permission was already provided by user before sign in step in order to proceed
    private void subscribeToData() {
        if (homePageViewModel.isCurrentUserSet()) {
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
                    formattedDevicePhoneNum = PhoneNumberFormatter.formatNumber(devicePhoneNumber, this);
                } catch (NumberParseException e) {
                    Toast.makeText(this, "FAILED to format phone number. Process failed", Toast.LENGTH_LONG).show();
                }

                if (!user.getPhoneNumber().equalsIgnoreCase(formattedDevicePhoneNum)
                        && user.getFingerprintAuthRegistration().equalsIgnoreCase("NO")) {
                    fingerprintAuthRegistrationText.setText(NO_COMPATIBILITY_WITH_PHONE);
                    btnGoToHomePage.setText("Go to home page");
                    btnGoToHomePage.setVisibility(View.VISIBLE);
                    return;
                }
                if (user.getFingerprintAuthRegistration().equalsIgnoreCase("NO")) {
                    fingerprintAuthRegistrationText.setText(PROPOSE_FINGERPRINT_AUTH);
                    btnFingerprintAuthAdd.setText("ADD");
                    btnFingerprintAuthMaybeLater.setText("MAYBE LATER");
                    btnFingerprintAuthAdd.setVisibility(View.VISIBLE);
                    btnFingerprintAuthMaybeLater.setVisibility(View.VISIBLE);
                    btnFingerprintAuthAdd.setOnClickListener(v -> {
                        user.setFingerprintAuthRegistration("YES");
                        homePageViewModel.updateUser(user);
                        fingerprintAuthRegistrationText.setText(IS_FINGERPRINT_AUTH_REGISTERED);
                        btnFingerprintAuthAdd.setVisibility(View.GONE);
                        btnFingerprintAuthMaybeLater.setVisibility(View.GONE);
                        btnStartFingerAuth.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "You now have the fingerprint authentication", Toast.LENGTH_LONG).show();
                    });

                } else if (user.getFingerprintAuthRegistration().equalsIgnoreCase("YES")) {
                    if (user.getPhoneNumber().equalsIgnoreCase(formattedDevicePhoneNum)) {
                        fingerprintAuthRegistrationText.setText(IS_FINGERPRINT_AUTH_REGISTERED);
                        btnFingerprintAuthAdd.setVisibility(View.GONE);
                        btnStartFingerAuth.setVisibility(View.VISIBLE);
                    } else {
                        fingerprintAuthRegistrationText.setText(NOT_ALLOWED);
                    }

                } else {
                    fingerprintAuthRegistrationText.setText("NOT ALLOWED");
                }

            });
        }
    }

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

            AuthResultText.setText("Biometric Not Supported on this phone");
            return;
        }
        biometricPrompt.authenticate(promptInfo);
    }

    public void goToHomePage() {
        Intent i = new Intent(FingerprintAuthenticationActivity.this, HomePageActivity.class);
        startActivity(i);
    }
}