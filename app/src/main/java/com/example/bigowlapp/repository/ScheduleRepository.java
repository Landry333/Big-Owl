package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Schedule;

public class ScheduleRepository extends Repository<Schedule> {

    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }
}
