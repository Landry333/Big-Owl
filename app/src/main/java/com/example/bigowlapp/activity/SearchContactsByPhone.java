package com.example.bigowlapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchContactsByPhone extends AppCompatActivity {
    private ListView listContactsView;
    private List<String> list, listShow;
    private Button btnSearch;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final ArrayList<QueryDocumentSnapshot> qds = new ArrayList<QueryDocumentSnapshot>();
    private EditText inputNumber;
    private String number2;

    EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_byphone);
        inputNumber = (EditText) findViewById(R.id.search_users);
        initialize();
    }

    protected void initialize() {
        loadContacts();
    }

    private void loadContacts() {
        list = new ArrayList<>();
        number = findViewById(R.id.search_users);

        btnSearch = findViewById(R.id.get_users);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();/*
                list.add(number.getText().toString());
                listShow = list;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listShow);
                listContactsView = findViewById(R.id.listContacts);
                listContactsView.setAdapter(adapter);*/

                number2 = inputNumber.getText().toString();

                db.collection("users")
                        .whereEqualTo("phoneNumber", number.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        list.clear();
                                        list.add(number.getText().toString());
                                        listShow = list;
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listShow);
                                        listContactsView = findViewById(R.id.listContacts);
                                        listContactsView.setAdapter(adapter);
                                        Toast.makeText(SearchContactsByPhone.this, "User found in the app system! This user has the app already. Please choose another user ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SearchContactsByPhone.this, "User doesn't have the app", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SearchContactsByPhone.this, SendSmsInvitationActivity.class);
                                        intent.putExtra("number1", number2);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });

        listShow = list;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listShow);
        listContactsView = findViewById(R.id.listContacts);
        listContactsView.setAdapter(adapter);

        //Check if users already has the app
        listContactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                String number = (String) listContactsView.getItemAtPosition(position);

                db.collection("users")
                        .whereEqualTo("phoneNumber", number.replaceAll("[^+0-9]", ""))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty())
                                        Toast.makeText(SearchContactsByPhone.this, "User has the app", Toast.LENGTH_SHORT).show();
                                    else {
                                        Toast.makeText(SearchContactsByPhone.this, "User doesn't have the app", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SearchContactsByPhone.this, SendSmsInvitationActivity.class);
                                        intent.putExtra("number1", number2);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });
/*
        number = findViewById(R.id.search_users);
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listShow);
                listContactsView.setAdapter((adapter));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/
    }


    private void searchUsers(String s) {
        List<String> filteredUsers = list.stream().filter(u -> {
            boolean containInName = u.toLowerCase().contains(s);
            boolean containInPhone = u.toLowerCase().contains(s);
            return (containInName || containInPhone);
        }).collect(Collectors.toList());

        listShow = filteredUsers;
    }
}