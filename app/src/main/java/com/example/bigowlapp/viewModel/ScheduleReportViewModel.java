package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Schedule;

import java.util.List;
import java.util.Map;

public class ScheduleReportViewModel extends BaseViewModel {
    private MutableLiveData<Schedule> scheduleData;

    public boolean isCurrentUserSupervisor(String supervisorId) {
        return isCurrentUserSet() && getCurrentUserUid().equals(supervisorId);
    }

    public LiveData<Schedule> getCurrentScheduleData(String scheduleId) {
        if (scheduleData == null) {
            scheduleData = new MutableLiveData<>();
            scheduleData = repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class);
        }
        return scheduleData;
    }

    public LiveData<Map<String, String>> getScheduleMemberNameMap(List<String> memberList) {
        return repositoryFacade.getUserRepository().getScheduleMemberIdNameMap(memberList);
    }
}
