package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Group;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class GroupRepository extends Repository<Group> {

    @Inject
    public GroupRepository(FirebaseFirestore mFirebaseFirestore) {
        super("groups", mFirebaseFirestore);
    }
}
