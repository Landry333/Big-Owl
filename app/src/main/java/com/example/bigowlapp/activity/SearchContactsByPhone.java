package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;

import java.util.ArrayList;
import java.util.List;

public class SearchContactsByPhone extends BigOwlActivity {
    private ListView listContactsView;
    private List<String> list, listShow;
    private Button btnSearch;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText number;
    private String smsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadContacts();
    }

    private void loadContacts() {
        list = new ArrayList<>();
        number = findViewById(R.id.search_users);

        btnSearch = findViewById(R.id.get_users);
        btnSearch.setOnClickListener(view -> {
            smsNumber = number.getText().toString();

            try {
                smsNumber = PhoneNumberFormatter.formatNumber(smsNumber, this);
            } catch (NumberParseException e) {
                number.setError(e.getMessage());
                number.requestFocus();
                return;
            }

            list.clear();

            db.collection(UserRepository.COLLECTION_NAME)
                    .whereEqualTo(User.Field.PHONE_NUMBER, smsNumber)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                list.clear();
                                list.add(number.getText().toString());
                                listShow = list;
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listShow);
                                listContactsView = findViewById(R.id.listContacts);
                                listContactsView.setAdapter(adapter);
                                Toast.makeText(SearchContactsByPhone.this, "User found in the app system! This user has the app already. Please choose another user ", Toast.LENGTH_SHORT).show();
                                User user = task.getResult().toObjects(User.class).get(0);
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
                        }
                    });
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_search_byphone;
    }
}