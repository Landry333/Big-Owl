package com.example.bigowlapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewUserRepository {
    private FirebaseAuth mfirebaseAuth;
    private FirebaseUser mfirebaseUser;
    private DatabaseReference mUserRef;
    private DatabaseReference supRequestRef;


    public ViewUserRepository() {
        supRequestRef = FirebaseDatabase.getInstance().getReference().child("SupRequests");
        if (mfirebaseAuth.getCurrentUser() != null) {
            mfirebaseUser = mfirebaseAuth.getCurrentUser();
        } else {
            //@TODO: return a error message, make an issue
            mfirebaseUser = null;
        }
    }

    public FirebaseUser getCurrentUser() {
        return mfirebaseUser;
    }

    public DatabaseReference getCurrentRequestRef() {
        return supRequestRef;
    }
}
