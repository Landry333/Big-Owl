package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Group;

public class GroupRepository extends Repository<Group> {
    public static final String COLLECTION_NAME = "groups";

    public GroupRepository() {
        super(GroupRepository.COLLECTION_NAME);
    }
}
