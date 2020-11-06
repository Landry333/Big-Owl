package com.example.bigowlapp.database;

import com.google.firebase.firestore.FirebaseFirestore;

//TODO: Remove after implementing DI
public class Firestore {
    // This is a singleton of the database instance
    public static FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }
}
