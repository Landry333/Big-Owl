package com.example.bigowlapp.repository;

import androidx.annotation.VisibleForTesting;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Model;
import com.example.bigowlapp.repository.exception.DocumentNotFoundException;
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

    protected final FirebaseFirestore mFirebaseFirestore;
    protected final CollectionReference collectionReference;

    protected Repository(String collectionName) {
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

    public LiveDataWithStatus<T> addDocument(T documentData) {
        LiveDataWithStatus<T> tData = new LiveDataWithStatus<>();
        collectionReference
                .add(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tData.setSuccess(documentData);
                    } else {
                        tData.setError(task.getException());
                    }
                });
        return tData;
    }

    public LiveDataWithStatus<T> addDocument(String docUid, T documentData) {
        LiveDataWithStatus<T> tData = new LiveDataWithStatus<>();
        collectionReference.document(docUid)
                .set(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tData.setSuccess(documentData);
                    } else {
                        tData.setError(task.getException());
                    }
                });
        return tData;
    }

    //===========================================================================================
    // Remove Document
    //===========================================================================================

    public LiveDataWithStatus<T> removeDocument(String docUid) {
        LiveDataWithStatus<T> tData = new LiveDataWithStatus<>();
        collectionReference.document(docUid)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tData.setSuccess(null);
                    } else {
                        tData.setError(task.getException());
                    }
                });
        return tData;
    }

    //===========================================================================================
    // Updating Document
    //===========================================================================================

    public LiveDataWithStatus<T> updateDocument(String docUid, T documentData) {
        LiveDataWithStatus<T> tData = new LiveDataWithStatus<>();
        collectionReference.document(docUid)
                .set(documentData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tData.setSuccess(documentData);
                    } else {
                        tData.setError(task.getException());
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
                        tData.setError(task.getException());
                    }
                });
        return tData;
    }

    public LiveDataWithStatus<T> getDocumentByAttribute(String attribute, String attrValue,
                                                        Class<? extends T> tClass) {
        LiveDataWithStatus<T> tData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo(attribute, attrValue)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDoc = task.getResult();
                        if (tDoc != null && !tDoc.isEmpty()) {
                            T t = tDoc.getDocuments().get(0).toObject(tClass);
                            tData.setSuccess(t);
                        } else {
                            tData.setError(getDocumentNotFoundException(tClass));
                        }
                    } else {
                        tData.setError(task.getException());
                    }
                });
        return tData;
    }

    //===========================================================================================
    // Fetching a list of Documents
    //===========================================================================================

    public LiveDataWithStatus<List<T>> getListOfDocumentByAttribute(String attribute, String attrValue,
                                                                    Class<? extends T> tClass) {
        LiveDataWithStatus<List<T>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo(attribute, attrValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setSuccess(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setError(getDocumentNotFoundException(tClass));
                        }
                    } else {
                        listOfTData.setError(task.getException());
                    }
                });
        return listOfTData;
    }

    public LiveDataWithStatus<List<T>> getListOfDocumentByArrayContains(String attribute, String attrValue,
                                                                        Class<? extends T> tClass) {
        LiveDataWithStatus<List<T>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereArrayContains(attribute, attrValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setSuccess(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setError(getDocumentNotFoundException(tClass));
                        }

                    } else {
                        listOfTData.setError(task.getException());
                    }
                });
        return listOfTData;
    }

    public LiveDataWithStatus<List<T>> getAllDocumentsFromCollection(Class<? extends T> tClass) {
        LiveDataWithStatus<List<T>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setSuccess(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setError(getDocumentNotFoundException(tClass));
                        }
                    } else {
                        listOfTData.setError(task.getException());
                    }
                });
        return listOfTData;
    }

    // TODO: bug where can only handle 10 items in the list, should allow any size list
    public LiveDataWithStatus<List<T>> getDocumentsByListOfUid(List<String> docUidList,
                                                               Class<? extends T> tClass) {
        LiveDataWithStatus<List<T>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereIn(FieldPath.documentId(), docUidList)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setSuccess(this.extractListOfDataToModel(task.getResult(), tClass));
                        } else {
                            listOfTData.setError(getDocumentNotFoundException(tClass));
                        }
                    } else {
                        listOfTData.setError(task.getException());
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

    /**
     * An exception that indicates that a document in the database either does not exist in the remote database
     *
     * @param tClass indicates the type of data that could not be found using class name
     * @return the exception with a message indicating the document could not be found
     */

    // TODO: Make private???
    public DocumentNotFoundException getDocumentNotFoundException(Class<? extends T> tClass) {
        return new DocumentNotFoundException("The " + tClass.getSimpleName() + " does not exist!");
    }
}
