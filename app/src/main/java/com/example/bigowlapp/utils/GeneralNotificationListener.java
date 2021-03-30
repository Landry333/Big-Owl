package com.example.bigowlapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

public class GeneralNotificationListener {
    private final FirebaseFirestore db;
    private RepositoryFacade repositoryFacade;

    public GeneralNotificationListener(){
        db = FirebaseFirestore.getInstance();
        repositoryFacade = RepositoryFacade.getInstance();
    }

}
