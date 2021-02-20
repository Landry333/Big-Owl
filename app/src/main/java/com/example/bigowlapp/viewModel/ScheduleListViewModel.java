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

    public LiveData<List<Schedule>> getScheduleList(String groupID) {
        if (scheduleData == null) {
            loadScheduleList(groupID);
        }
        return scheduleData;
    }

    private void loadScheduleList(String groupID) {
        scheduleData = repositoryFacade.getScheduleRepository()
                .getListSchedulesFromGroupForUser(groupID, getCurrentUserUid());
    }
}
