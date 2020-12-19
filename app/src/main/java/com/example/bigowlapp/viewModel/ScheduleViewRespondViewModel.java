package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.ScheduleRepository;

import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.google.firebase.Timestamp.now;

public class ScheduleViewRespondViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final NotificationRepository notificationRepository;
    private final ScheduleRepository scheduleRepository;
    private MutableLiveData<Schedule> scheduleData;
    private Schedule.UserResponse currentUserNewResponse;
    private static final int ONE_MINUTE = 60000;

    // TODO: Dependency Injection
    public ScheduleViewRespondViewModel() {
        authRepository = new AuthRepository();
        notificationRepository = new NotificationRepository();
        scheduleRepository = new ScheduleRepository();
    }

    public void respondSchedule(String scheduleId, Response response) {
        if (isCurrentUserInSchedule()) {
            currentUserNewResponse = getUserResponseInSchedule();
            currentUserNewResponse.setResponse(response);
            currentUserNewResponse.setResponseTime(now());
            scheduleRepository.updateScheduleMemberResponse(scheduleId, authRepository.getCurrentUser().getUid(), currentUserNewResponse);
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
        Notification newNotification = new Notification();
        newNotification.setSenderUId(authRepository.getCurrentUser().getUid());
        newNotification.setReceiverUId(scheduleData.getValue().getGroupSupervisorUId());
        newNotification.setGroupUId(scheduleData.getValue().getGroupUId());
        newNotification.setType("memberResponseSchedule");
        newNotification.setTimeRead(null);
        newNotification.setTimeSend(now());
        newNotification.setSenderResponse(currentUserNewResponse);
        notificationRepository.addDocument(newNotification);
    }

    public boolean isOneMinuteAfterLastResponse() {
        long userLastResponseTime = getUserResponseInSchedule().getResponseTime().toDate().getTime();
        return now().toDate().getTime() >= (userLastResponseTime + ONE_MINUTE);
    }
}
