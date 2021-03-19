package com.example.bigowlapp.repository;

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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public LiveData<Map<String, String>> getScheduleMemberNameMap(List<String> memberList) {
        Map<String, String> memberNameAttend = new HashMap<>();
        MutableLiveData<Map<String, String>> memberNameAttendData = new MutableLiveData<>();
        collectionReference.whereIn(FieldPath.documentId(), memberList).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    memberNameAttend.put(
                            document.getId(),
                            document.getString("firstName") + " " + document.getString("lastName"));
                }
                memberNameAttendData.setValue(memberNameAttend);
            }
        });
        return memberNameAttendData;
    }
}
