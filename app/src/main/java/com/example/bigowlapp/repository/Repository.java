package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Model;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T extends Model> {

    private final FirebaseFirestore mFirebaseFirestore;
    private final CollectionReference collectionReference;

    protected Repository(String collectionName) {
        mFirebaseFirestore = Firestore.getDatabase();
        collectionReference = mFirebaseFirestore.collection(collectionName);
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

    public MutableLiveData<T> addDocument(String docUid, T documentData) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        tData.setValue(documentData);
        collectionReference.document(docUid)
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

    // TODO: Change this method's return type
    public boolean removeDocument(String docUid) {
        collectionReference.document(docUid)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document removed successfully.");
                    } else {
                        Log.e(getClassName(), "Error removing document: " +
                                task.getException());
                    }
                });
        return false;
    }

    //===========================================================================================
    // Updating Document
    //===========================================================================================

    public MutableLiveData<T> updateDocument(String docUid, T documentData) {
        MutableLiveData<T> tData = new MutableLiveData<>();
        tData.setValue(documentData);
        collectionReference.document(docUid)
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
    // Fetching a Document
    //===========================================================================================

    public LiveDataWithStatus<T> getDocumentByUid(String docUid, Class<? extends T> tClass) {
        LiveDataWithStatus<T> tData = new LiveDataWithStatus<>();
        collectionReference.document(docUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot tDoc = task.getResult();
                        if (tDoc != null && tDoc.exists()) {
                            T t = tDoc.toObject(tClass);
                            tData.setSuccess(t);
                        } else {
                            tData.setError(getDocumentNotFoundException(tClass));
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());

                        tData.setError(task.getException());
                    }
                });
        return tData;
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

    // TODO: bug where can only handle 10 items in the list, should allow any size list
    public MutableLiveData<List<T>> getDocumentsByListOfUid(List<String> docUidList,
                                                            Class<? extends T> tClass) {
        MutableLiveData<List<T>> listOfTData = new MutableLiveData<>();
        collectionReference.whereIn(FieldPath.documentId(), docUidList)
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

    private List<T> extractListOfDataToModel(QuerySnapshot results, Class<? extends T> tClass) {
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

    public Exception getDocumentNotFoundException(Class<? extends T> tClass) {
        return new Exception("The " + tClass.getSimpleName() + "data does not exist!");
    }
}
