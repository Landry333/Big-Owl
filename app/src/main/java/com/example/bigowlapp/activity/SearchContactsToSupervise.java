package com.example.bigowlapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchContactsToSupervise extends BigOwlActivity {
    private ListView listContactsView;
    private List<String> list, listShow;
    private Button loadContacts;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final ArrayList<QueryDocumentSnapshot> qds = new ArrayList<QueryDocumentSnapshot>();

    EditText search_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    protected void initialize() {
        loadContacts();
    }

    private void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        list = new ArrayList<>();

        if (cursor.getCount() > 0) {
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

                        list.add(name + "\n" + PhoneNumber);
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

        //Check if users already has the app
        listContactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                String contactDetails = (String) listContactsView.getItemAtPosition(position);
                String contactNumber;
                if (contactDetails.split("\\n").length < 2) {
                    contactNumber = contactDetails
                            .split("\\n")[0]
                            .replaceAll("[^+0-9]", "");
                } else {
                    contactNumber = contactDetails
                            .split("\\n")[1]
                            .replaceAll("[^+0-9]", "");
                }

                db.collection("users")
                        .whereEqualTo("phoneNumber", contactNumber)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()){
                                        Toast.makeText(SearchContactsToSupervise.this, "This user has the app already. Please choose another user", Toast.LENGTH_SHORT).show();
                                        User user = task.getResult().toObjects(User.class).get(0);
                                        Intent intent = new Intent(SearchContactsToSupervise.this, ViewAnotherUserActivity.class);
                                        intent.putExtra("user", user);
                                        intent.putExtra("contactDetails", contactDetails);
                                        startActivity(intent);

                                    }

                                    else {
                                        Toast.makeText(SearchContactsToSupervise.this, "User doesn't have the app", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SearchContactsToSupervise.this, SendSmsInvitationActivity.class);
                                        intent.putExtra("contactDetails", contactDetails);
                                        intent.putExtra("contactNumber", contactNumber);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });

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

    @Override
    public int getContentView() {
        return R.layout.activity_search_contacts;
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