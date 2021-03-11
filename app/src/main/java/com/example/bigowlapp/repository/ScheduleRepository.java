package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class ScheduleRepository extends Repository<Schedule> {
    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }

    public Task<Void> updateScheduleMemberResponse(String scheduleId, String userUid, UserScheduleResponse currentUserScheduleResponse) {
        return collectionReference.document(scheduleId)
                .update((Schedule.Field.USER_SCHEDULE_RESPONSE_MAP + ".").concat(userUid),
                        currentUserScheduleResponse);
    }

    public LiveDataWithStatus<List<Schedule>> getListSchedulesFromGroupForUser(String groupID, String userID) {
        LiveDataWithStatus<List<Schedule>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo(Schedule.Field.GROUP_UID, groupID)
                .whereArrayContains(Schedule.Field.MEMBER_LIST, userID)
                .orderBy(Schedule.Field.START_TIME, Query.Direction.ASCENDING)
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

    /**
     * Queries the list of schedules for the user in which all schedules have a startTime greater than
     * the date today. The list of schedules are ordered by startTime in ascending order.
     *
     * @param userID The id of the user
     * @return A Task that contains the QuerySnapshot of the list of schedule for the given user
     */
    public Task<QuerySnapshot> getTaskListSchedulesForUser(String userID) {
        return collectionReference.whereArrayContains(Schedule.Field.MEMBER_LIST, userID)
                .whereGreaterThanOrEqualTo(Schedule.Field.START_TIME, Timestamp.now())
                .orderBy(Schedule.Field.START_TIME, Query.Direction.ASCENDING)
                .get();
    }
}