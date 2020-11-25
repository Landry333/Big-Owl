package com.example.bigowlapp.viewModel;

import android.content.Context;
import android.widget.Toast;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.google.firebase.Timestamp.now;

public class ScheduleResponseViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private MutableLiveData<Schedule> scheduleData;

    // TODO: Dependency Injection
    public ScheduleResponseViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        scheduleRepository = new ScheduleRepository();
    }

    public void responseSchedule(String scheduleId, Response response) {
        if (isCurrentUserInSchedule()) {
            Schedule.UserResponse currentUserResponse = getUserResponseInSchedule();
            currentUserResponse.setResponse(response);
            currentUserResponse.setResponseTime(now());
            scheduleRepository.updateScheduleMemberResponse(scheduleId, authRepository.getCurrentUser().getUid(), currentUserResponse);
            scheduleData.setValue(scheduleData.getValue());
        }
    }

    public Schedule.UserResponse getUserResponseInSchedule() {
        return Objects.requireNonNull(scheduleData.getValue()).getMembers()
                .get(authRepository.getCurrentUser().getUid());
    }

    public boolean isCurrentUserInSchedule() {
        return Objects.requireNonNull(scheduleData.getValue()).getMembers()
                .containsKey(authRepository.getCurrentUser().getUid());
    }

    public LiveData<Schedule> getCurrentScheduleData(String scheduleId) {
        if (scheduleData == null) {
            scheduleData = new MutableLiveData<>();
            scheduleData = scheduleRepository.getDocumentByUId(scheduleId, Schedule.class);
        }
        return scheduleData;
    }

    public void notifySupervisorScheduleResponse() {

    }
}
