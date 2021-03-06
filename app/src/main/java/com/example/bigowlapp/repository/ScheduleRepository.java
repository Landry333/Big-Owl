package com.example.bigowlapp.repository;

import androidx.annotation.VisibleForTesting;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class ScheduleRepository extends Repository<Schedule> {
    public static final String COLLECTION_NAME = "schedules";

    public ScheduleRepository() {
        super(ScheduleRepository.COLLECTION_NAME);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public ScheduleRepository(FirebaseFirestore db, CollectionReference collectionReference) {
        super(db, collectionReference);
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
                .addOnCompleteListener(task -> resolveTaskWithListResult(task, listOfTData, Schedule.class));
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

    public LiveDataWithStatus<List<Schedule>> getAllSchedulesForSupervisor(String currentUserUid) {
        LiveDataWithStatus<List<Schedule>> listOfTData = new LiveDataWithStatus<>();
        collectionReference.whereEqualTo(Schedule.Field.GROUP_SUPERVISOR_UID, currentUserUid)
                .orderBy(Schedule.Field.START_TIME, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> resolveTaskWithListResult(task, listOfTData, Schedule.class));
        return listOfTData;
    }

    public Task<QuerySnapshot> getTaskListSchedulesForSupervisor(String userID) {
        return collectionReference.whereEqualTo(Schedule.Field.GROUP_SUPERVISOR_UID, userID)
                .whereGreaterThanOrEqualTo(Schedule.Field.START_TIME, Timestamp.now())
                .orderBy(Schedule.Field.START_TIME, Query.Direction.ASCENDING)
                .get();
    }
}