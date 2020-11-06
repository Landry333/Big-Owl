package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class UserRepository extends Repository<User> {

    @Inject
    public UserRepository(FirebaseFirestore mFirebaseFirestore) {
        super("users", mFirebaseFirestore);
    }
}
