package com.example.bigowlapp.repository;

import com.example.bigowlapp.utils.NotificationListenerManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private final FirebaseAuth mfirebaseAuth;

    public AuthRepository() {
        mfirebaseAuth = FirebaseAuth.getInstance();
    }

    public void addAuthStateListener(FirebaseAuth.AuthStateListener authStateListener) {
        mfirebaseAuth.addAuthStateListener(authStateListener);
    }

    public Task<AuthResult> signUpUser(String email, String password) {
        return mfirebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInUser(String email, String password) {
        return mfirebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void signOutUser() {
        mfirebaseAuth.signOut();
        NotificationListenerManager.stopListening();
    }

    public FirebaseUser getCurrentUser() {
        return mfirebaseAuth.getCurrentUser();
    }
}