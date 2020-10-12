package com.example.bigowlapp.repository;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public abstract class Repository<T> {
    String getClassName() {
        return this.getClass().toString();
    }

    public abstract MutableLiveData<List<T>> getAllDocumentsFromCollection();
}
