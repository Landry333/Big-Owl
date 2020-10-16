package com.example.bigowlapp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.Model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchContactsToSupervise extends AppCompatActivity {
   // private TextView listContacts;
    private ListView listContactsView;
    private List<String> list, listShow;
    private Button loadContacts;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<QueryDocumentSnapshot> qds = new ArrayList<QueryDocumentSnapshot>();

    EditText search_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        initialize();
    }

    protected void initialize() {
        //listContacts = (TextView) findViewById(R.id.listContacts);
        loadContacts = (Button) findViewById(R.id.loadContacts);
        loadContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadContacts();
            }
        });

    }

    private void loadContacts() {
        //StringBuilder builder = new StringBuilder();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,null,null,null);

        list = new ArrayList<>();

        if(cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    Cursor cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                            new String[]{id}, null);

                    while (cursor2.moveToNext()) {
                        String PhoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //builder.append("Contact : ").append(name).append(", Phone Number : ").append(PhoneNumber).append("\n\n");

                        list.add("Contact: " + name + "\", Phone Number : \"" + PhoneNumber);
                        Log.d("test1","test11");
                    }

                    cursor2.close();
                }
            }
        }
        cursor.close();

        listShow = list;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, listShow);
        listContactsView = findViewById(R.id.listContacts);
        listContactsView.setAdapter(adapter);

        //listContacts.setText(builder.toString());


        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
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
        });
    }


    private void searchUsers(String s) {
/*
        if(s == null || s.equals(""))
        {
            mUsersShow = mUsers;
            return;
        }*/
        List<String> filteredUsers = list.stream().filter(u -> {
            boolean containInName = u.toLowerCase().contains(s);
            boolean containInPhone = u.toLowerCase().contains(s);
            return (containInName || containInPhone);
        }).collect(Collectors.toList());

        listShow = filteredUsers;
    }
}