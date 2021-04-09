package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.example.bigowlapp.view_model.SearchContactsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;

public class SearchContactsByPhone extends BigOwlActivity {
    private Button btnSearch;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText number;
    private String smsNumber;

    private SearchContactsViewModel searchContactsViewModel;
    private PhoneNumberFormatter phoneNumberFormatter;


    @Override
    public int getContentView() {
        return R.layout.activity_search_byphone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = findViewById(R.id.search_users);
        btnSearch = findViewById(R.id.get_users);

        phoneNumberFormatter = new PhoneNumberFormatter(this);

        loadContacts();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (searchContactsViewModel == null) {
            searchContactsViewModel = new ViewModelProvider(this)
                    .get(SearchContactsViewModel.class);
        }
    }

    private void loadContacts() {
        btnSearch.setOnClickListener(view -> {
            smsNumber = number.getText().toString();

            try {
                smsNumber = phoneNumberFormatter.formatNumber(smsNumber);
            } catch (NumberParseException e) {
                number.setError(e.getMessage());
                number.requestFocus();
                return;
            }

            searchContactsViewModel.getUserToAdd(smsNumber).observe(this, user -> {
                Intent intent;
                if (user == null) {
                    Toast.makeText(this, "User doesn't have the app", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, SendSmsInvitationActivity.class);
                    intent.putExtra("contactDetails", smsNumber);
                    intent.putExtra("contactNumber", smsNumber);
                } else {
                    Toast.makeText(this, "User found in the app system! This user has the app already. Please choose another user ", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, SendingRequestToSuperviseActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("contactDetails", smsNumber);
                }
                startActivity(intent);
            });
        });
    }
}