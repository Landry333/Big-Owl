package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.NotificationListenerManager;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.example.bigowlapp.viewModel.SignUpViewModel;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class SignUpPageActivity extends AppCompatActivity {
    public EditText userEmail, userPassword, userPhone, userFirstName, userLastName;
    Button btnSignUp;
    TextView tvSignIn;
    private SignUpViewModel signUpViewModel;
    private ProgressBar progressBar;
    private NotificationListenerManager invitationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(signUpViewModel == null){
            signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        }

        initialize();
    }

    protected void initialize() {
        //Authentication with firebase
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress_bar);

        userFirstName = findViewById(R.id.user_first_name);
        userLastName = findViewById(R.id.user_last_name);
        userEmail = findViewById(R.id.edit_text_text_mail_address);
        userPassword = findViewById(R.id.edit_text_text_password);
        userPhone = findViewById(R.id.edit_text_phone);
        btnSignUp = findViewById(R.id.button_sign_up);
        tvSignIn = findViewById(R.id.text_view_sign_in);

        btnSignUp.setOnClickListener(v -> {
            String email = userEmail.getText().toString();
            String pass = userPassword.getText().toString();
            String userPhone = this.userPhone.getText().toString();
            String firstName = userFirstName.getText().toString();
            String lastName = userLastName.getText().toString();

            String formattedUserPhone;
            try {
                formattedUserPhone = PhoneNumberFormatter.formatNumber(userPhone, this);
            } catch (NumberParseException e) {
                this.userPhone.setError(e.getMessage());
                this.userPhone.requestFocus();
                return;
            }

            //Error handling
            if (firstName.isEmpty()) {
                userFirstName.setError("Please enter your first name");
                userFirstName.requestFocus();
            } else if (lastName.isEmpty()) {
                userLastName.setError("Please enter your last name");
                userLastName.requestFocus();
            } else if (email.isEmpty()) {
                userEmail.setError("Please enter a valid email");
                userEmail.requestFocus();
            } else if (pass.isEmpty()) {
                userPassword.setError("Please enter your password");
                userEmail.requestFocus();
            } else if (userPhone.isEmpty()) {
                this.userPhone.setError("please enter a phone number");
                this.userPhone.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                signUpViewModel.createUser(email, pass, formattedUserPhone, firstName, lastName)
                        .addOnSuccessListener(isSuccessful -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpPageActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                            invitationListener = new NotificationListenerManager();
                            invitationListener.listen(this);
                            startActivity(new Intent(SignUpPageActivity.this, HomePageActivity.class));
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SignUpPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        });
            }
        });

        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpPageActivity.this, LoginPageActivity.class));
        });
    }

    @VisibleForTesting
    public void setSignUpViewModel(SignUpViewModel signUpViewModel) {
        this.signUpViewModel = signUpViewModel;
    }
}