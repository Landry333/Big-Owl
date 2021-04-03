package com.example.bigowlapp.activity;

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
    private static int INDEX_CONTACT_NAME_NEW = 0;
    private static int INDEX_CONTACT_NUMBER = 1;

    private final static String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
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
    private Cursor phoneResultsCursor;

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
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.e("BigOwl", "name");
        phoneResultsCursor = cursor;

        int numResults = 0;
        list = new ArrayList<>();

        while (phoneResultsCursor.moveToNext()) {
            String name = phoneResultsCursor.getString(INDEX_CONTACT_NAME_NEW);
            String number = phoneResultsCursor.getString(INDEX_CONTACT_NUMBER);

            list.add(name + "\n" + number);
            ++numResults;

            if (numResults >= MAX_RESULTS) {
                updateList();
                phoneResultsCursor.close();
                loaderManager.destroyLoader(0);
                return;
            }
        }

        updateList();
        phoneResultsCursor.close();
        loaderManager.destroyLoader(0);
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