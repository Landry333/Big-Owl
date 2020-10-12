package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.database.Firestore;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository extends Repository<Group> {

    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference groupsRef;

    // TODO: Dependency Injection Implementation for Firestore
    public GroupRepository() {
        mFirebaseFirestore = Firestore.getDatabase();
        groupsRef = mFirebaseFirestore.collection("groups");
    }

    //===========================================================================================
    // Adding Document
    //===========================================================================================

    @Override
    public MutableLiveData<Group> addDocument(String docUId, Group documentData) {
        MutableLiveData<Group> groupData = new MutableLiveData<>();
        groupData.setValue(documentData);
        groupsRef.document(docUId)
                .set(documentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error adding document: ", task.getException());
                    }
                });
        return groupData;
    }

    //===========================================================================================
    // Updating Document
    //===========================================================================================

    @Override
    public MutableLiveData<Group> updateDocument(String docUId, Group documentData) {
        MutableLiveData<Group> groupData = new MutableLiveData<>();
        groupData.setValue(documentData);
        groupsRef.document(docUId)
                .set(documentData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(getClassName(), "Document added successfully.");
                    } else {
                        Log.e(getClassName(), "Error updating document: ", task.getException());
                    }
                });
        return groupData;
    }

    //===========================================================================================
    // Fetching Document
    //===========================================================================================

    // Fetch the group from Firestore using the UId of the group
    public MutableLiveData<Group> getGroupByUId(String UId) {
        MutableLiveData<Group> groupData = new MutableLiveData<>();
        groupsRef.document(UId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot groupDoc = task.getResult();
                        if (groupDoc != null && groupDoc.exists()) {
                            Group group = groupDoc.toObject(Group.class);
                            groupData.setValue(group);
                        } else {
                            groupData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return groupData;
    }

    // Fetch the group from Firestore using the name variable
    public MutableLiveData<User> getGroupByName(String name) {
        MutableLiveData<User> groupData = new MutableLiveData<>();
        groupsRef.whereEqualTo("name", name)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot groupDocs = task.getResult();
                        if (groupDocs != null && !groupDocs.isEmpty()) {
                            User user = groupDocs.getDocuments().get(0).toObject(User.class);
                            groupData.setValue(user);
                        } else {
                            groupData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return groupData;
    }

    // Fetch the group from Firestore using the monitoringUserId variable
    public MutableLiveData<Group> getGroupByMonitoringUserId(String monitoringUserId) {
        MutableLiveData<Group> groupData = new MutableLiveData<>();
        groupsRef.whereEqualTo("monitoringUserId", monitoringUserId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot groupDocs = task.getResult();
                        if (groupDocs != null && !groupDocs.isEmpty()) {
                            Group group = groupDocs.getDocuments().get(0).toObject(Group.class);
                            groupData.setValue(group);
                        } else {
                            groupData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return groupData;
    }

    // Fetch the group from Firestore using the supervisedUserId variable
    public MutableLiveData<List<Group>> getListOfGroupsBySupervisedUserId(String supervisedUserId) {
        MutableLiveData<List<Group>> listOfGroupData = new MutableLiveData<>();
        groupsRef.whereArrayContains("supervisedUserId", supervisedUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot groupDocs = task.getResult();
                        if (groupDocs != null && !groupDocs.isEmpty()) {
                            List<Group> listOfGroups = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Group group = doc.toObject(Group.class);
                                listOfGroups.add(group);
                            }
                            listOfGroupData.setValue(listOfGroups);
                        } else {
                            listOfGroupData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfGroupData;
    }

    // Fetch all groups from the database
    @Override
    public MutableLiveData<List<Group>> getAllDocumentsFromCollection() {
        MutableLiveData<List<Group>> listOfGroupData = new MutableLiveData<>();
        groupsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot groupDocs = task.getResult();
                        if (groupDocs != null && !groupDocs.isEmpty()) {
                            List<Group> listOfGroups = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Group group = doc.toObject(Group.class);
                                listOfGroups.add(group);
                            }
                            listOfGroupData.setValue(listOfGroups);
                        } else {
                            listOfGroupData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfGroupData;
    }
}
