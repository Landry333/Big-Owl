package com.example.bigowlapp.database;

import com.google.firebase.firestore.FirebaseFirestore;

public class Firestore {

    private Firestore() {
        // Only need one instance of firebase; Not used as object
    }

    // This is a singleton of the database instance
    public static FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }
}
