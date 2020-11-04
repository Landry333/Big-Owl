package com.example.bigowlapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewUserRepository {
    private FirebaseAuth mfirebaseAuth;
    private FirebaseUser mfirebaseUser;
    private DatabaseReference mUserRef;
    private DatabaseReference supRequestRef;


    public ViewUserRepository(){
        supRequestRef = FirebaseDatabase.getInstance().getReference().child("SupRequests");
        mfirebaseUser = mfirebaseAuth.getCurrentUser();
    }

    public FirebaseAuth getMfirebaseAuth() {
        return mfirebaseAuth;
    }

    public DatabaseReference getmUserRef() {
        return mUserRef;
    }

    public FirebaseUser getMfirebaseUser() {
        return mfirebaseUser;
    }

    public DatabaseReference getSupRequestRef() {
        return supRequestRef;
    }
}
