package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Group;

public class GroupRepository extends Repository<Group> {

    // TODO: Dependency Injection Implementation for Firestore
    public GroupRepository() {
        super("groups");
    }
}
