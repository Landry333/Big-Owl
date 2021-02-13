package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository extends Repository<User> {

    // TODO: Dependency Injection Implementation for Firestore
    public UserRepository() {
        super("users");
    }

    public Task<Void> isPhoneNumberUnique(String phoneNumber) {
        LiveDataWithStatus<User> tData = new LiveDataWithStatus<>();
        Task<QuerySnapshot> getPhoneNumberTask =
                collectionReference.whereEqualTo("phoneNumber", phoneNumber)
                        .limit(1)
                        .get();

        Task<Void> isSuccessfulTask = getPhoneNumberTask.continueWithTask(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot tDoc = task.getResult();
                if (tDoc != null && !tDoc.isEmpty()) {
                    User t = tDoc.getDocuments().get(0).toObject(User.class);
                    tData.setSuccess(t);
                } else {
                    tData.setError(getDocumentNotFoundException(User.class));
                }
            } else {
                tData.setError(task.getException());
            }
        });


        return isSuccessfulTask;
    }
}
