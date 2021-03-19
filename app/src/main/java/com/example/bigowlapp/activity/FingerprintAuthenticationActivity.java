package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.bigowlapp.viewModel.HomePageViewModel;

import java.util.concurrent.Executor;

public class FingerprintAuthenticationActivity extends AppCompatActivity {

    private TextView textView;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;
    private Button btnStartFingerAuth;
    private Button btnLogOut;
    private HomePageViewModel homePageViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authenticaton);

        textView = findViewById(R.id.authentication_message);
        btnStartFingerAuth = findViewById(R.id.btnStartAuthentication);
        btnLogOut = findViewById(R.id.btn_logout);
        executor = ContextCompat.getMainExecutor(this);

        //Toast.makeText(FingerprintAuthenticationActivity.this, "Authenticate fingerprint to get access", Toast.LENGTH_LONG).show();

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                textView.setText("Error");
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //textView.setText("Success");
                //Toast.makeText(FingerprintAuthenticationActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(FingerprintAuthenticationActivity.this, HomePageActivity.class);
                startActivity(i);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                textView.setText("Failure");
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




    public void fingerAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {

            textView.setText("Biometric Not Supported");
            return;
        }
        biometricPrompt.authenticate(promptInfo);
    }
}