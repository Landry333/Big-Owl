package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.database.Firestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Repository<T> {

    protected final FirebaseFirestore mFirebaseFirestore;
    protected final CollectionReference collectionReference;

    public Repository(String collectionName) {
        mFirebaseFirestore = Firestore.getDatabase();
        collectionReference = mFirebaseFirestore.collection(collectionName);
    }

    // TODO: Remove or Modify when dependency injection implemented
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public Repository(FirebaseFirestore mFirebaseFirestore, CollectionReference collectionReference) {
        this.mFirebaseFirestore = mFirebaseFirestore;
        this.collectionReference = collectionReference;
    }

    //===========================================================================================
    // Adding Document
    //===========================================================================================

    public MutableLiveData<T> addDocument(T documentData) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        tData.setValue(documentData);
        collectionReference
                .add(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error adding document: " +
                                task.getException());
                    }
                });
        return tData;
    }

    public MutableLiveData<T> addDocument(String docUId, T documentData) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        tData.setValue(documentData);
        collectionReference.document(docUId)
                .set(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error adding document: " +
                                task.getException());
                    }
                });
        return tData;
    }

    //===========================================================================================
    // Remove Document
    //===========================================================================================

    // TODO: Check for Asynchronous boolean value
    public boolean removeDocument(String docUId) {
        // We are using AtomicBoolean because the lambda function is asynchronous.
        // Thus we need an atomic variable, so we can set the boolean value in the lambda function.
        AtomicBoolean isSuccessful = new AtomicBoolean();
        collectionReference.document(docUId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document removed successfully.");
                        isSuccessful.set(true);
                    } else {
                        Log.e(getClassName(), "Error removing document: " +
                                task.getException());
                        isSuccessful.set(false);
                    }
                });
        return isSuccessful.get();
    }

    //===========================================================================================
    // Updating Document
    //===========================================================================================

    public MutableLiveData<T> updateDocument(String docUId, T documentData) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        tData.setValue(documentData);
        collectionReference.document(docUId)
                .set(documentData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error updating document: " +
                                task.getException());
                    }
                });
        return tData;
    }

    //===========================================================================================
    // Fetching a Documents
    //===========================================================================================

    // TODO: incorrect naming convention -------------V
    public MutableLiveData<T> getDocumentByUId(String UId, Class<? extends T> tClass) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        collectionReference.document(UId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot tDoc = task.getResult();
                        if (tDoc != null && tDoc.exists()) {
                            T t = tDoc.toObject(tClass);
                            tData.setValue(t);
                        } else {
                            tData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return tData;
    }

    // TODO: bug where can only handle 10 items in the list, should allow any size list
    public MutableLiveData<List<T>> getDocumentsByListOfUId(List<String> uIdList, Class<? extends T> tClass) {
        MutableLiveData<List<T>> listOfTData = new MutableLiveData<>();
        collectionReference.whereIn(FieldPath.documentId(), uIdList)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setValue(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }

    public MutableLiveData<T> getDocumentByAttribute(String attribute, String attrValue,
                                                     Class<? extends T> tClass) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        collectionReference.whereEqualTo(attribute, attrValue)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDoc = task.getResult();
                        if (tDoc != null && !tDoc.isEmpty()) {
                            T t = tDoc.getDocuments().get(0).toObject(tClass);
                            tData.setValue(t);
                        } else {
                            tData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return tData;
    }

    //===========================================================================================
    // Fetching a list of Documents
    //===========================================================================================

    public MutableLiveData<List<T>> getListOfDocumentByAttribute(String attribute, String attrValue,
                                                                 Class<? extends T> tClass) {
        MutableLiveData<List<T>> listOfTData = new MutableLiveData<>();
        collectionReference.whereEqualTo(attribute, attrValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setValue(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }

    public MutableLiveData<List<T>> getListOfDocumentByArrayContains(String attribute, String attrValue,
                                                                     Class<? extends T> tClass) {
        MutableLiveData<List<T>> listOfTData = new MutableLiveData<>();
        collectionReference.whereArrayContains(attribute, attrValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setValue(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setValue(null);
                        }

                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }

    public MutableLiveData<List<T>> getAllDocumentsFromCollection(Class<? extends T> tClass) {
        MutableLiveData<List<T>> listOfTData = new MutableLiveData<>();
        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setValue(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }

    protected List<T> extractListOfDataToModel(QuerySnapshot results, Class<? extends T> tClass) {
        List<T> listOfT = new ArrayList<>();
        for (QueryDocumentSnapshot doc : results) {
            T t = doc.toObject(tClass);
            listOfT.add(t);
        }
        return listOfT;
    }

    String getClassName() {
        return this.getClass().toString();
    }
}
