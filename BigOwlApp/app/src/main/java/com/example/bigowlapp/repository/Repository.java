package com.example.bigowlapp.repository;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public abstract class Repository<T> {

    String getClassName() {
        return this.getClass().toString();
    }

    abstract MutableLiveData<List<T>> getAllDocumentsFromCollection();

    abstract MutableLiveData<T> updateDocument(String docUId, T documentData);

    abstract MutableLiveData<T> addDocument(String docUId, T documentData);

//    abstract MutableLiveData<T> getDocumentByUId();
//
//    abstract MutableLiveData<T> getDocumentByAttribute(String attribute);
//
//    abstract MutableLiveData<List<T>> getListOfDocumentByAttribute();
}
