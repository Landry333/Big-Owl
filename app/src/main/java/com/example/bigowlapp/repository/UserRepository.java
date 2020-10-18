package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;

public class UserRepository extends Repository<User> {

    // TODO: Dependency Injection Implementation for Firestore
    public UserRepository() {
        super("users");
    }
}
