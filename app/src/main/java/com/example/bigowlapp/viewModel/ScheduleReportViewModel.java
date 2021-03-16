package com.example.bigowlapp.viewModel;

import android.location.Address;
import android.location.Geocoder;

import com.example.bigowlapp.activity.ScheduleReportActivity;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;

import java.io.IOException;
import java.util.HashMap;
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

    public Map<String, Boolean> getMemberNameDidAttendMap(ScheduleReportActivity scheduleReportActivity, Schedule schedule) {
        LiveData<List<User>> groupMembers = repositoryFacade.getUserRepository()
                .getListOfDocumentByArrayContains("memberGroupIdList", schedule.getGroupUid(), User.class);

        Map<String, Boolean> memberNameAttend = new HashMap<>();

        groupMembers.observe(scheduleReportActivity, userList -> {
            for (User user : userList) {
                memberNameAttend.put(user.getFullName(), schedule.getUserScheduleResponseMap().get(user.getUid()).getAttendance().didAttend());
            }
        });

        return memberNameAttend;
    }

    public String getScheduleLocation(ScheduleReportActivity scheduleReportActivity, Schedule schedule) {
        List<Address> address = null;
        try {
            address = new Geocoder(scheduleReportActivity)
                    .getFromLocation(schedule.getLocation().getLatitude(),
                            schedule.getLocation().getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address.get(0).getAddressLine(0);
    }
}
