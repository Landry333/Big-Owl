package com.example.bigowlapp.ActivityPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPageActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth m_FirebaseAuth;
    private FirebaseAuth.AuthStateListener m_AuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    protected void initialize()
    {
        try
        {
            m_FirebaseAuth = FirebaseAuth.getInstance();
            emailId = findViewById(R.id.editTextTextEmailAddress);
            password = findViewById(R.id.editTextTextPassword);
            btnSignIn = findViewById(R.id.button);
            tvSignUp = findViewById(R.id.textView);

            m_AuthStateListener = new FirebaseAuth.AuthStateListener()
            {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser m_FirebaseUser = m_FirebaseAuth.getCurrentUser();
                    if(m_FirebaseUser != null)
                    {
                        Toast.makeText(LoginPageActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginPageActivity.this, HomePageActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(LoginPageActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            btnSignIn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String email = emailId.getText().toString();
                    String pass = password.getText().toString();



                    if(email.isEmpty())
                    {
                        emailId.setError("Please enter a valid email");
                        emailId.requestFocus();
                    }
                    else if(pass.isEmpty())
                    {
                        password.setError("Please enter your password");
                        password.requestFocus();
                    }
                    else if(!(email.isEmpty() && pass.isEmpty()))
                    {
                        m_FirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginPageActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(LoginPageActivity.this, "Login error, Please login again", Toast.LENGTH_SHORT).show();
                                }
                                //if successful sign up
                                else
                                {
                                    Intent i = new Intent(LoginPageActivity.this, HomePageActivity.class);
                                    startActivity(i);
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LoginPageActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvSignUp.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginPageActivity.this, SignUpPageActivity.class);
                    startActivity(i);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    protected void onStart() {
        try
        {
            super.onStart();
            m_FirebaseAuth.addAuthStateListener(m_AuthStateListener);
        }
        catch (Exception ex)
        {

        }
    }
}