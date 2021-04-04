package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.example.bigowlapp.repository.exception.EmptyFieldException;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;

public class SearchContactsByPhone extends BigOwlActivity {
    private Button btnSearch;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText number;
    private String smsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = findViewById(R.id.search_users);
        btnSearch = findViewById(R.id.get_users);

        loadContacts();
    }

    private void loadContacts() {
        btnSearch.setOnClickListener(view -> {
            smsNumber = number.getText().toString();

            try {
                smsNumber = PhoneNumberFormatter.formatNumber(smsNumber, this);
            } catch (NumberParseException | EmptyFieldException e) {
                number.setError(e.getMessage());
                number.requestFocus();
                return;
            }

            db.collection(UserRepository.COLLECTION_NAME)
                    .whereEqualTo(User.Field.PHONE_NUMBER, smsNumber)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(result -> {
                        if (!result.isEmpty()) {
                            Toast.makeText(SearchContactsByPhone.this, "User found in the app system! This user has the app already. Please choose another user ", Toast.LENGTH_SHORT).show();
                            User user = result.toObjects(User.class).get(0);
                            Intent intent = new Intent(SearchContactsByPhone.this, SendingRequestToSuperviseActivity.class);
                            intent.putExtra("user", user);
                            intent.putExtra("contactDetails", smsNumber);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SearchContactsByPhone.this, "User doesn't have the app", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SearchContactsByPhone.this, SendSmsInvitationActivity.class);
                            intent.putExtra("contactDetails", smsNumber);
                            intent.putExtra("contactNumber", smsNumber);
                            startActivity(intent);
                        }
                    });
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_search_byphone;
    }
}