package com.example.bigowlapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchContactsToSupervise extends BigOwlActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int INDEX_CONTACT_ID = 0;
    private static int INDEX_CONTACT_LOOKUP_KEY = 1;
    private static int INDEX_CONTACT_NAME = 2;
    private static int INDEX_CONTACT_HAS_NUMBER = 3;

    private final static String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
//            ContactsContract.Data.CONTACT_ID,
    };

    private static final String SELECTION =
            ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

    private static final int MAX_RESULTS = 10;

    // Defines a variable for the search string
    private String searchString = "";
    // Defines the array to hold values that replace the ?
    private final String[] selectionArgs = {searchString};

    private ListView listContactsView;
    private List<String> list;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText searchUsers;

    private ArrayAdapter<String> contactsAdapter;
    private long selectedContactId;

    private LoaderManager loaderManager;

    @Override
    public int getContentView() {
        return R.layout.activity_search_contacts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listContactsView = findViewById(R.id.listContacts);
        searchUsers = findViewById(R.id.search_users);

        loaderManager = LoaderManager.getInstance(this);

        initialize();
    }

    protected void initialize() {
        list = new ArrayList<>();
        updateList();

        setupContactListClick();
        setupSearchListener();

        loaderManager.initLoader(0, null, this);
    }

    private void setupSearchListener() {
        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchString = charSequence.toString();
                loaderManager.restartLoader(0, null, SearchContactsToSupervise.this);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setupContactListClick() {
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

                db.collection(UserRepository.COLLECTION_NAME)
                        .whereEqualTo(User.Field.PHONE_NUMBER, contactNumber)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        Toast.makeText(SearchContactsToSupervise.this, "This user has the app already. Please choose another user", Toast.LENGTH_SHORT).show();
                                        User user = task.getResult().toObjects(User.class).get(0);
                                        Intent intent = new Intent(SearchContactsToSupervise.this, SendingRequestToSuperviseActivity.class);
                                        intent.putExtra("user", user);
                                        intent.putExtra("contactDetails", contactDetails);
                                        startActivity(intent);

                                    } else {
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
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.e("BigOwl", "gogo");


        // Puts the search string into the selection criteria
        selectionArgs[0] = "%" + searchString + "%";

        // Starts the query
        // TODO: right ow uses selection to search by name only, consider using all possible
        return new CursorLoader(
                this,
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.e("BigOwl", "name");

        int numResults = 0;
        list = new ArrayList<>();

        while (cursor.moveToNext()) {
            boolean hasNumber = cursor.getInt(INDEX_CONTACT_HAS_NUMBER) == 1;
            if (!hasNumber) {
                continue;
            }

            long contactId = cursor.getLong(INDEX_CONTACT_ID);
            String name55 = cursor.getString(INDEX_CONTACT_NAME);

            ContentResolver contentResolver = getContentResolver();
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contactId)},
                    null);

            while (phones.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                list.add(name + "\n" + number);
                ++numResults;

                if (numResults >= MAX_RESULTS) {
                    phones.close();
                    cursor.close();
                    updateList();
                    return;
                }
            }

            phones.close();
        }
        cursor.close();
        updateList();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.e("BigOwl", "rest");
    }

    private void updateList() {
        contactsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                list);

        listContactsView.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();
    }
}