package com.example.bigowlapp.viewModel;

import android.location.Address;
import android.location.Geocoder;

import com.example.bigowlapp.activity.ScheduleReportActivity;
import com.example.bigowlapp.model.Schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public String getScheduleLocation(ScheduleReportActivity scheduleReportActivity, Schedule schedule) {
        List<Address> address = new ArrayList<>();
        try {
            address = new Geocoder(scheduleReportActivity)
                    .getFromLocation(schedule.getLocation().getLatitude(),
                            schedule.getLocation().getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address.get(0).getAddressLine(0);
    }

    public LiveData<Map<String, String>> getScheduleMemberNameMap(List<String> memberList) {
        return repositoryFacade.getUserRepository().getScheduleMemberNameMap(memberList);
    }
}
