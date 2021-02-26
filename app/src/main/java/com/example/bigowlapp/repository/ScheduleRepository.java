package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class ScheduleRepository extends Repository<Schedule> {

    private static final String USER_SCHEDULE_RESPONSE_MAP = "userScheduleResponseMap";

    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }

    public Task<Void> updateScheduleMemberResponse(String scheduleId, String userUid, UserScheduleResponse currentUserScheduleResponse) {
        return collectionReference.document(scheduleId).update((USER_SCHEDULE_RESPONSE_MAP + ".").concat(userUid), currentUserScheduleResponse);
    }

    public LiveDataWithStatus<List<Schedule>> getListSchedulesFromGroupForUser(String groupID, String userID) {
        LiveDataWithStatus<List<Schedule>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo("groupUid", groupID)
                .whereArrayContains("memberList", userID)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setSuccess(this.extractListOfDataToModel(task.getResult(), Schedule.class));
                        } else {
                            listOfTData.setError(getDocumentNotFoundException(Schedule.class));
                        }
                    } else {
                        listOfTData.setError(task.getException());
                    }
                });
        return listOfTData;
    }

    public Task<QuerySnapshot> getTaskListSchedulesForUser(String userID) {
        return collectionReference.whereArrayContains("memberList", userID)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get();
    }
}