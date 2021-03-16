package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.exception.PhoneNumberTakenException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public Map<String, Boolean> getMemberNameAttendanceMapFromSchedule(Map<String, UserScheduleResponse> map) {
        List<String> scheduleMemberList = new ArrayList<>(map.keySet());
        Map<String, Boolean> memberNameAttend = new HashMap<>();
        collectionReference.whereIn(FieldPath.documentId(), scheduleMemberList).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    memberNameAttend.put(
                            document.getString("firstName") + " " + document.getString("lastName"),
                            Objects.requireNonNull(map.get(document.getId())).getAttendance().didAttend());
                }
            }
        });
        return memberNameAttend;
    }
}
