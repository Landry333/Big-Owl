package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Schedule;

import java.util.List;

public class ScheduleListViewModel extends BaseViewModel {
    private MutableLiveData<List<Schedule>> scheduleData;

    public ScheduleListViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public LiveData<List<Schedule>> getScheduleList(Boolean isGroupSupervisor, String groupID) {
        if (scheduleData == null) {
            loadScheduleList(isGroupSupervisor, groupID);
        }
        return scheduleData;
    }

    private void loadScheduleList(Boolean isGroupSupervisor, String groupID) {
        if (isGroupSupervisor)
            scheduleData = repositoryFacade.getScheduleRepository()
                    .getAllSchedulesForSupervisor(getCurrentUserUid());
        else
            scheduleData = repositoryFacade.getScheduleRepository()
                    .getListSchedulesFromGroupForUser(groupID, getCurrentUserUid());
    }
}
