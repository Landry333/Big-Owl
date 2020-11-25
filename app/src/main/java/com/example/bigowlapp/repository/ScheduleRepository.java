package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Schedule;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class ScheduleRepository extends Repository<Schedule> {

    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }

    public Task<Void> updateScheduleMemberResponse(String scheduleId, String userUId, Schedule.UserResponse currentUserResponse) {
        return collectionReference.document(scheduleId).update("members.".concat(userUId), currentUserResponse);
    }
}