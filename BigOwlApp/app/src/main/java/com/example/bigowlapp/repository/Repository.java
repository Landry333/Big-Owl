package com.example.bigowlapp.repository;

public abstract class Repository<T> {
    String getClassName() {
        return this.getClass().toString();
    }
}
