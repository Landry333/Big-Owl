package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class ScheduleRepository extends Repository<Schedule> {

    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }

    public Task<Void> updateScheduleMemberResponse(String scheduleId, String userUId, UserScheduleResponse currentUserScheduleResponse) {
        return collectionReference.document(scheduleId).update("members.".concat(userUId), currentUserScheduleResponse);
    }

    public LiveDataWithStatus<List<Schedule>> getListSchedulesFromGroupForUser(String groupID, String userID) {
        LiveDataWithStatus<List<Schedule>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo("groupUId", groupID)
                .whereArrayContains("membersList", userID)
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


}