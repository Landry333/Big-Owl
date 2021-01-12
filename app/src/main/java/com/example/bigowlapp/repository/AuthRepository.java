package com.example.bigowlapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private FirebaseAuth mfirebaseAuth;

    // TODO: Dependency Injection Implementation for Firestore
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

    // TODO: When deleting a user, other entries in the database should be deleted
    public Task<Void> deleteUser() {
        return mfirebaseAuth.getCurrentUser().delete();
    }

    public void signOutUser() {
        mfirebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return mfirebaseAuth.getCurrentUser();
    }
}