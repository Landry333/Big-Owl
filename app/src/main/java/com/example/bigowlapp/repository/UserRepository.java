package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.exception.PhoneNumberTakenException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository extends Repository<User> {

    public static final String COLLECTION_NAME = "users";

    // TODO: Dependency Injection Implementation for Firestore
    public UserRepository() {
        super(UserRepository.COLLECTION_NAME);
    }

    public Task<Void> isPhoneNumberInDatabase(String phoneNumber) {
        Task<QuerySnapshot> taskGetPhoneNumber =
                collectionReference.whereEqualTo(User.Field.PHONE_NUMBER, phoneNumber)
                        .limit(1)
                        .get();

        return taskCheckIfPhoneNumberExists(taskGetPhoneNumber);
    }

    private Task<Void> taskCheckIfPhoneNumberExists(Task<QuerySnapshot> taskPrevious) {
        return taskPrevious.continueWithTask(task -> {
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
