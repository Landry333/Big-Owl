package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerprintAuthenticationActivity extends BigOwlActivity {

    private TextView textView;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprintAuthenticationActivity);

        textView = findViewById(R.id.textView);
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                textView.setText("Error");
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                textView.setText("Success");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                textView.setText("Failure");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Programmer World Authentication")
                .setNegativeButtonText("Cancel/ Use Password")
                .setConfirmationRequired(false)
                .build();
    }

    @Override
    protected int getContentView() {

    }

    public void buttonAuthenticate(View view) {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {

            textView.setText("Biometric Not Supported");
            return;
        }
        biometricPrompt.authenticate(promptInfo);
    }
}