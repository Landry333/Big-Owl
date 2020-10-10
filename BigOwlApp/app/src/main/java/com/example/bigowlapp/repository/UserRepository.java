package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.User;

import static android.content.ContentValues.TAG;

public class UserRepository {

    private FirebaseFirestore mFirebaseFirestore;

    public UserRepository() {
        mFirebaseFirestore = Firestore.getDatabase();
    }

    // TODO: Solve Asynchronous problem
    public MutableLiveData<User> getUserByPhoneNumber(String phoneNumber) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        mFirebaseFirestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("USERREPO", "Pekora2 = :3");
                            User u = task.getResult().getDocuments().get(0).toObject(User.class);
                            MutableLiveData<User> userData = new MutableLiveData<>();
                            userData.setValue(u);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return userData;
    }

}
