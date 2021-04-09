package com.example.bigowlapp.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Schedule;

import java.util.List;

public class ScheduleListViewModel extends BaseViewModel {
    private MutableLiveData<List<Schedule>> scheduleData;

    public ScheduleListViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public LiveData<List<Schedule>> getScheduleList(boolean isGroupSupervisor, String groupID) {
        if (scheduleData == null) {
            loadScheduleList(isGroupSupervisor, groupID);
        }
        return scheduleData;
    }

    private void loadScheduleList(boolean isGroupSupervisor, String groupID) {
        if (isGroupSupervisor)
            scheduleData = repositoryFacade.getScheduleRepository()
                    .getAllSchedulesForSupervisor(getCurrentUserUid());
        else
            scheduleData = repositoryFacade.getScheduleRepository()
                    .getListSchedulesFromGroupForUser(groupID, getCurrentUserUid());
    }
}
