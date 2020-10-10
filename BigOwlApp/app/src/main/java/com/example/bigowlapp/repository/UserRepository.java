package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class UserRepository {

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference usersRef;

    public UserRepository() {
            mFirebaseFirestore = Firestore.getDatabase();
            usersRef = mFirebaseFirestore.collection("users");
    }

    public MutableLiveData<User> getUserByPhoneNumber(String phoneNumber) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        usersRef.whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            User user = userTask.getResult().getDocuments().get(0).toObject(User.class);
                            userData.setValue(user);
                        } else {
                            Log.d(TAG, "Error getting documents: ", userTask.getException());
                        }
                });
        return userData;
    }
}
