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

    private static final String USER_SCHEDULE_RESPONSE_MAP = "userScheduleResponseMap";
    private static final String START_TIME = "startTime";
    private static final String MEMBER_LIST = "memberList";
    private static final String GROUP_ID = "groupUid";

    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }

    public Task<Void> updateScheduleMemberResponse(String scheduleId, String userUid, UserScheduleResponse currentUserScheduleResponse) {
        return collectionReference.document(scheduleId).update((USER_SCHEDULE_RESPONSE_MAP + ".").concat(userUid), currentUserScheduleResponse);
    }

    public LiveDataWithStatus<List<Schedule>> getListSchedulesFromGroupForUser(String groupID, String userID) {
        LiveDataWithStatus<List<Schedule>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo(GROUP_ID, groupID)
                .whereArrayContains(MEMBER_LIST, userID)
                .orderBy(START_TIME, Query.Direction.ASCENDING)
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
     * @param userID The id of the user
     * @return A Task that contains the QuerySnapshot of the list of schedule for the given user
     */
    public Task<QuerySnapshot> getTaskListSchedulesForUser(String userID) {
        return collectionReference.whereArrayContains(MEMBER_LIST, userID)
                .whereGreaterThanOrEqualTo(START_TIME, Timestamp.now())
                .orderBy(START_TIME, Query.Direction.ASCENDING)
                .get();
    }
    public Task<QuerySnapshot> getTaskListSchedulesForSupervisor(String userID) {
        return collectionReference.whereEqualTo("groupSupervisorUid", userID)
                .whereGreaterThanOrEqualTo(START_TIME, Timestamp.now())
                .orderBy(START_TIME, Query.Direction.ASCENDING)
                .get();
    }
}