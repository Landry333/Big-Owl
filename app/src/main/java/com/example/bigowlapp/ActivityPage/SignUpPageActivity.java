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
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpPageActivity extends AppCompatActivity {
    public EditText emailId, password, phone, name;
    private User user = new User();
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth m_FirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

   protected void initialize()
    {
        try
        {
            //Authentication with firebase
            m_FirebaseAuth = FirebaseAuth.getInstance();
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
                    else if(userPhone.isEmpty())
                    {
                        phone.setError("please enter a phone number");
                        phone.requestFocus();
                    }
                    else if(email.isEmpty() || pass.isEmpty() || userPhone.isEmpty())
                    {
                        Toast.makeText(SignUpPageActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                    }
                    else if(!(email.isEmpty() && pass.isEmpty() &&  userPhone.isEmpty()))
                    {
                        m_FirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpPageActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(SignUpPageActivity.this, "SignUp Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                                }
                                //if successful sign up
                                else
                                {
                                    user.setEmail(email);
                                    user.setPhoneNumber(userPhone);
                                    user.setFirstName(userName);
                                    //user.setUId();
                                    startActivity(new Intent(SignUpPageActivity.this, HomePageActivity.class));
                                }
                            }
                        });
                    }
                    else
                    {
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
        }
        catch(Exception ex)
        {

        }
    }
}