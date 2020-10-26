package com.example.bigowlapp.repository;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bigowlapp.activityPage.HomePageActivity;
import com.example.bigowlapp.activityPage.LoginPageActivity;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
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
    public Task<Boolean> signUpUser(String email, String password, String phoneNumber, String name) {
        User user = new User();
        Task<AuthResult> taskAuthResult = mfirebaseAuth.createUserWithEmailAndPassword(email, password);
        Task<Boolean> taskBoolean = taskAuthResult.continueWithTask(task -> {
            if (task.isSuccessful()) {
                user.setUId(this.getCurrentUser().getUid());
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setFirstName(nameExtractor(name)[0]);
                user.setLastName(nameExtractor(name)[1]);
                return Tasks.forResult(true);
            } else {
                throw task.getException();
            }
        });
        return taskBoolean;


    }

    public Task<Boolean> signInUser(String email, String password) {
        Task<AuthResult> taskAuthResult = mfirebaseAuth.signInWithEmailAndPassword(email, password);
        Task<Boolean> taskBoolean = taskAuthResult.continueWithTask(task -> {
            if (task.isSuccessful()) {
                return Tasks.forResult(true);
            } else {
                return Tasks.forResult(false);
            }
        });
        return taskBoolean;
    }

    // TODO: Preferably better to use boolean return type
    public void deleteUser() {
        if (this.getCurrentUser() != null) {
            this.getCurrentUser()
                    .delete()
                    .addOnCompleteListener(task -> {
                        Log.d(this.getClassName(), "User has been deleted");

                    });
        }
    }


    public void signOutUser() {
        mfirebaseAuth.signOut();
    }

    public String nameParcer(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String[] nameExtractor(String name) {
        return name.split(" ", 2);
    }

    String getClassName() {
        return this.getClass().toString();
    }
}