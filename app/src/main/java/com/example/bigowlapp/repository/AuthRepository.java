package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

// TODO: Instead of a boolean use a string return value
public class AuthRepository {

    private FirebaseAuth mfirebaseAuth;
    private UserRepository userRepository;

    @Inject
    public AuthRepository(FirebaseAuth mfirebaseAuth, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.mfirebaseAuth = mfirebaseAuth;
    }

    public FirebaseUser getCurrentUser() {
        return mfirebaseAuth.getCurrentUser();
    }

    // TODO: Handle exceptions concerning the failure of the "user" database collection
    public Task<Boolean> signUpUser(String email, String password, String phoneNumber,
                                    String firstName, String lastName) {
        User user = new User();
        Task<AuthResult> taskAuthResult = mfirebaseAuth.createUserWithEmailAndPassword(email, password);
        Task<Boolean> taskBoolean = taskAuthResult.continueWithTask(task -> {
            if (task.isSuccessful()) {
                String uId = this.getCurrentUser().getUid();
                user.setUId(uId);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                this.userRepository.addDocument(uId, user);
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
                throw task.getException();
            }
        });
        return taskBoolean;
    }

    // TODO: When deleting a user, other entries in the database should be deleted
    public Task<Boolean> deleteUser() {
        Task<Void> taskVoidDeletion = this.getCurrentUser().delete();
        Task<Boolean> taskBoolean = taskVoidDeletion.continueWithTask(task -> {
            if (task.isSuccessful()) {
                return Tasks.forResult(true);
            } else {
                throw task.getException();
            }
        });
        return taskBoolean;
    }
    
    public void addAuthStateListener(FirebaseAuth.AuthStateListener authStateListener){
        mfirebaseAuth.addAuthStateListener(authStateListener);
    }

    public void signOutUser() {
        mfirebaseAuth.signOut();
    }

    private String getClassName() {
        return this.getClass().toString();
    }
}