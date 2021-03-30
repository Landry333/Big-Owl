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

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.AuthFailureNotificationListener;
import com.example.bigowlapp.viewModel.LogInViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPageActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private LogInViewModel logInViewModel;
    private AuthFailureNotificationListener authListener;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logInViewModel = new ViewModelProvider(this).get(LogInViewModel.class);
        initialize();
    }

    protected void initialize() {

        try {
            //BiometricManager biometricManager = BiometricManager.from(this);
            progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

            emailId = findViewById(R.id.editTextTextEmailAddress);
            password = findViewById(R.id.editTextTextPassword);
            btnSignIn = findViewById(R.id.button);
            tvSignUp = findViewById(R.id.textView);

            mAuthStateListener = firebaseAuth -> {
                FirebaseUser m_FirebaseUser = logInViewModel.getCurrentUser();
                if (m_FirebaseUser != null) {
                    checkIfCanFingerprintAuthenticate();
                    //Toast.makeText(LoginPageActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    /*Intent i = new Intent(LoginPageActivity.this, HomePageActivity.class);
                    authListener = new AuthFailureNotificationListener();
                    authListener.listen(this);
                    startActivity(i);*/
                    /*
                    Toast.makeText(LoginPageActivity.this, "Authenticate fingerprint to get access", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(LoginPageActivity.this, HomePageActivity.class);
                    i.putExtra("login message","You are logged in");
                    startActivity(i);*/
                } else {
                    Toast.makeText(LoginPageActivity.this, "Please login", Toast.LENGTH_LONG).show();
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
                                    /*Intent i;
                                    if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){
                                        Toast.makeText(LoginPageActivity.this, "you are logged in!", Toast.LENGTH_SHORT).show();
                                        i = new Intent(LoginPageActivity.this, HomePageActivity.class);
                                        //i.putExtra("login message","Successfully logged in!");
                                    }
                                    else{
                                        Toast.makeText(LoginPageActivity.this, "user ID and password accepted", Toast.LENGTH_LONG).show();
                                        i = new Intent(LoginPageActivity.this, FingerprintAuthenticationActivity.class);
                                    }
                                    startActivity(i);*/
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

    public void checkIfCanFingerprintAuthenticate(){
        BiometricManager biometricManager = BiometricManager.from(this);
        Intent i;
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS){
            Toast.makeText(LoginPageActivity.this, "you are logged in!", Toast.LENGTH_SHORT).show();
            i = new Intent(LoginPageActivity.this, HomePageActivity.class);
            //i.putExtra("login message","Successfully logged in!");
        }
        else{
            Toast.makeText(LoginPageActivity.this, "user ID and password accepted", Toast.LENGTH_SHORT).show();
            i = new Intent(LoginPageActivity.this, FingerprintAuthenticationActivity.class);
        }
        startActivity(i);
    }


}