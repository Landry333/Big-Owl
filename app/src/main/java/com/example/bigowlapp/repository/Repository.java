package com.example.bigowlapp.repository;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Model;
import com.example.bigowlapp.repository.exception.DocumentNotFoundException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T extends Model> {

    protected final FirebaseFirestore mFirebaseFirestore;
    protected CollectionReference collectionReference;

    protected Repository(String collectionName) {
        mFirebaseFirestore = Firestore.getDatabase();
        collectionReference = mFirebaseFirestore.collection(collectionName);
    }

    public ListenerRegistration listenToCollection(EventListener<QuerySnapshot> listener) {
        return collectionReference.addSnapshotListener(listener);
    }

    //===========================================================================================
    // Adding Document
    //===========================================================================================

    public <X extends T> LiveDataWithStatus<X> addDocument(X documentData) {
        LiveDataWithStatus<X> tData = new LiveDataWithStatus<>();
        collectionReference
                .add(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        documentData.setUid(task.getResult().getId());
                        tData.setSuccess(documentData);
                    } else {
                        tData.setError(task.getException());
                    }
                });
        return tData;
    }

    public <X extends T> LiveDataWithStatus<X> addDocument(String docUid, X documentData) {
        LiveDataWithStatus<X> tData = new LiveDataWithStatus<>();
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

    public <X extends T> LiveDataWithStatus<X> updateDocument(String docUid, X documentData) {
        LiveDataWithStatus<X> tData = new LiveDataWithStatus<>();
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

    public <X extends T> LiveDataWithStatus<X> getDocumentByUid(String docUid, Class<X> tClass) {
        LiveDataWithStatus<X> tData = new LiveDataWithStatus<>();
        collectionReference.document(docUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot tDoc = task.getResult();
                        if (tDoc != null && tDoc.exists()) {
                            X t = tDoc.toObject(tClass);
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

    public <X extends T> LiveDataWithStatus<X> getDocumentByAttribute(String attribute, String attrValue,
                                                                      Class<X> tClass) {
        LiveDataWithStatus<X> tData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo(attribute, attrValue)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDoc = task.getResult();
                        if (tDoc != null && !tDoc.isEmpty()) {
                            X t = tDoc.getDocuments().get(0).toObject(tClass);
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

    public <X extends T> LiveDataWithStatus<List<X>> getListOfDocumentByAttribute(String attribute, String attrValue,
                                                                                  Class<X> tClass) {
        LiveDataWithStatus<List<X>> listOfTData = new LiveDataWithStatus<>();
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

    public <X extends T> LiveDataWithStatus<List<X>> getListOfDocumentByArrayContains(String attribute, String attrValue,
                                                                                      Class<X> tClass) {
        LiveDataWithStatus<List<X>> listOfTData = new LiveDataWithStatus<>();
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

    public <X extends T> LiveDataWithStatus<List<X>> getAllDocumentsFromCollection(Class<X> tClass) {
        LiveDataWithStatus<List<X>> listOfTData = new LiveDataWithStatus<>();
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
    public <X extends T> LiveDataWithStatus<List<X>> getDocumentsByListOfUid(List<String> docUidList,
                                                                             Class<X> tClass) {
        LiveDataWithStatus<List<X>> listOfTData = new LiveDataWithStatus<>();
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

    protected <X extends T> List<X> extractListOfDataToModel(QuerySnapshot results, Class<X> tClass) {
        List<X> listOfT = new ArrayList<>();
        for (QueryDocumentSnapshot doc : results) {
            X t = doc.toObject(tClass);
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
    protected DocumentNotFoundException getDocumentNotFoundException(Class<? extends T> tClass) {
        return new DocumentNotFoundException("The " + tClass.getSimpleName() + " does not exist!");
    }
}
