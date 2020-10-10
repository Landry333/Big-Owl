package com.example.bigowlapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth m_FirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize();
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
    }

    protected void initialize()
    {
        try
        {
            //Authentication with firebase
            m_FirebaseAuth = FirebaseAuth.getInstance();
            emailId = findViewById(R.id.editTextTextEmailAddress);
            password = findViewById(R.id.editTextTextPassword);
            btnSignUp = findViewById(R.id.button);
            tvSignIn = findViewById(R.id.textView);

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailId.getText().toString();
                    String pass = password.getText().toString();

                    //Error handling
                    if(email.isEmpty())
                    {
                        emailId.setError("Please enter a valid email");
                        emailId.requestFocus();
                    }
                    else if(pass.isEmpty())
                    {
                        password.setError("Please enter your password");
                        emailId.requestFocus();
                    }
                    else if(email.isEmpty() && pass.isEmpty())
                    {
                        Toast.makeText(MainActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                    }
                    else if(!(email.isEmpty() && pass.isEmpty()))
                    {
                        m_FirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(MainActivity.this, "SignUp Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
        }
        catch(Exception ex)
        {

        }
    }
}