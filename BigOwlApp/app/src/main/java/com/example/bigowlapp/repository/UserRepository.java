package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository extends Repository {

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference usersRef;

    public UserRepository() {
        mFirebaseFirestore = Firestore.getDatabase();
        usersRef = mFirebaseFirestore.collection("users");
    }

    public MutableLiveData<User> getUserById(String id) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        usersRef.document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDoc = task.getResult();
                        if(userDoc != null && userDoc.exists()){
                            User user = userDoc.toObject(User.class);
                            userData.setValue(user);
                        }else {
                            userData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: ", task.getException());
                    }
                });
        return userData;
    }


    public MutableLiveData<User> getUserByPhoneNumber(String phoneNumber) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        usersRef.whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot userDocs = task.getResult();
                        if(userDocs != null && !userDocs.isEmpty()){
                            User user = userDocs.getDocuments().get(0).toObject(User.class);
                            userData.setValue(user);
                        }else {
                            userData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: ", task.getException());
                    }
                });
        return userData;
    }

}
