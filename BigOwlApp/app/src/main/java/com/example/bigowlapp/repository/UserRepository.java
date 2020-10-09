package com.example.bigowlapp.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.User;

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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            User u = task.getResult().getDocuments().get(0).toObject(User.class);
                            MutableLiveData<User> userData = new MutableLiveData<>();
                            userData.setValue(u);
                        }else{
                            // TODO: Log the error
                        }
                    }
                });
        return userData;
    }

}
