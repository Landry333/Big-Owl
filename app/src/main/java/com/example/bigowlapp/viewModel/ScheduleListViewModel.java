package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;

import java.util.List;

public class ScheduleListViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private MutableLiveData<List<Schedule>> scheduleData;
    private Schedule.UserResponse currentUserNewResponse;

    public ScheduleListViewModel() {
        authRepository = new AuthRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
        scheduleRepository = new ScheduleRepository();
    }

    public LiveData<List<Schedule>> getScheduleList(String groupID) {
        if (scheduleData == null) {
            loadScheduleList(groupID);
        }
        return scheduleData;
    }

    private void loadScheduleList(String groupID) {
        scheduleData = scheduleRepository.getListSchedulesFromGroupForUser(groupID, authRepository.getCurrentUser().getUid());
    }
}
