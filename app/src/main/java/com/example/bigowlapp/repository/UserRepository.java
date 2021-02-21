package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.exception.PhoneNumberTakenException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository extends Repository<User> {

    // TODO: Dependency Injection Implementation for Firestore
    public UserRepository() {
        super("users");
    }

    public Task<Void> isPhoneNumberInDatabase(String phoneNumber) {
        Task<QuerySnapshot> taskGetPhoneNumber =
                collectionReference.whereEqualTo("phoneNumber", phoneNumber)
                        .limit(1)
                        .get();

        return taskGetPhoneNumber.continueWithTask(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot tDoc = task.getResult();
                if (tDoc != null && !tDoc.isEmpty()) {
                    throw new PhoneNumberTakenException();
                } else {
                    return Tasks.forResult(null);
                }
            } else {
                throw task.getException();
            }
        });
    }
}
