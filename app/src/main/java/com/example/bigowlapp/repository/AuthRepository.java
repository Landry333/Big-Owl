package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicBoolean;


// TODO: Sign up / Sign in / Delete / Sign out /
public class AuthRepository {

    private FirebaseAuth mfirebaseAuth;
    private UserRepository userRepository;

    // TODO: Dependency Injection Implementation for Firestore
    public AuthRepository() {
        mfirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mfirebaseAuth.getCurrentUser();
    }

    // TODO: SAVE USER TO CLOUD FIRESTORE
    // TODO: Check for Asynchronous boolean value
    public boolean signUpUser(String email, String password, String phoneNumber, String name) {
        User user = new User();
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        mfirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.setUId(this.getCurrentUser().getUid());
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setFirstName(nameExtractor(name)[0]);
                user.setLastName(nameExtractor(name)[1]);
                isSuccess.set(true);
            } else {
                isSuccess.set(false);
            }
        });
        return isSuccess.get();
    }

    public void signInUser() {
    }

    public void signOutUser(){
        mfirebaseAuth.signOut();
    }

    public String nameParcer(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String[] nameExtractor(String name) {
        return name.split(" ", 2);
    }
}