package com.example.bigowlapp.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.exception.PhoneNumberTakenException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository extends Repository<User> {
    public static final String COLLECTION_NAME = "users";

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

    public LiveData<Map<String, String>> getScheduleMemberIdNameMap(List<String> memberList) {
        MutableLiveData<Map<String, String>> mapData = new MutableLiveData<>();
        collectionReference.whereIn(FieldPath.documentId(), memberList).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, String> map = new HashMap<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    map.put(document.getId(), document.getString("firstName") + " " + document.getString("lastName"));
                }
                mapData.setValue(map);
            }
        });
        return mapData;
    }
}
