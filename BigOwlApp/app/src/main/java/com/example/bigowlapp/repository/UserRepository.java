package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class UserRepository extends Repository<User> {

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference usersRef;

    // TODO: Dependency Injection Implementation for Firestore
    public UserRepository() {
        mFirebaseFirestore = Firestore.getDatabase();
        usersRef = mFirebaseFirestore.collection("users");
    }

    //===========================================================================================
    // Adding Document
    //===========================================================================================

    @Override
    public MutableLiveData<User> addDocument(String docUId, User documentData) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        userData.setValue(documentData);
        usersRef.document(docUId)
                .set(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error updating document: ", task.getException());
                    }
                });
        return userData;
    }

    //===========================================================================================
    // Updating Document
    //===========================================================================================

    @Override
    public MutableLiveData<User> updateDocument(String docUId, User documentData) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        userData.setValue(documentData);
        usersRef.document(docUId)
                .set(documentData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error updating document: ", task.getException());
                    }
                });
        return userData;
    }

    //===========================================================================================
    // Fetching Document
    //===========================================================================================

    // Fetch the user from Firestore using the UId of the user
    public MutableLiveData<User> getUserByUId(String UId) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        usersRef.document(UId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDoc = task.getResult();
                        if (userDoc != null && userDoc.exists()) {
                            User user = userDoc.toObject(User.class);
                            userData.setValue(user);
                        } else {
                            userData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: ", task.getException());
                    }
                });
        return userData;
    }

    // Fetch the user from Firestore using the phoneNumber variable
    public MutableLiveData<User> getUserByPhoneNumber(String phoneNumber) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        usersRef.whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot userDocs = task.getResult();
                        if (userDocs != null && !userDocs.isEmpty()) {
                            User user = userDocs.getDocuments().get(0).toObject(User.class);
                            userData.setValue(user);
                        } else {
                            userData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: ", task.getException());
                    }
                });
        return userData;
    }

    // Fetch the user from Firestore using the phoneNumber variable
    public MutableLiveData<User> getUserByEmail(String email) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        usersRef.whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot userDocs = task.getResult();
                        if (userDocs != null && !userDocs.isEmpty()) {
                            User user = userDocs.getDocuments().get(0).toObject(User.class);
                            userData.setValue(user);
                        } else {
                            userData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: ", task.getException());
                    }
                });
        return userData;
    }

    // Fetch all users from the database
    @Override
    public MutableLiveData<List<User>> getAllDocumentsFromCollection() {
        MutableLiveData<List<User>> listUsersData = new MutableLiveData<>();
        usersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot userDocs = task.getResult();
                        if (userDocs != null && !userDocs.isEmpty()) {
                            List<User> listOfUsers = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                User user = doc.toObject(User.class);
                                listOfUsers.add(user);
                            }
                            listUsersData.setValue(listOfUsers);
                        } else {
                            listUsersData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: ", task.getException());
                    }
                });
        return listUsersData;
    }
}
