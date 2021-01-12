package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.ScheduleRequest;
import com.example.bigowlapp.model.UserScheduleResponse;

import java.util.Objects;

import static com.google.firebase.Timestamp.now;

public class ScheduleViewRespondViewModel extends BaseViewModel {

    private MutableLiveData<Schedule> scheduleData;
    private UserScheduleResponse currentUserNewResponse;
    private static final int ONE_MINUTE = 60000;

    // TODO: Dependency Injection
    public ScheduleViewRespondViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public void respondSchedule(String scheduleId, Response response) {
        if (isCurrentUserInSchedule()) {
            currentUserNewResponse = getUserScheduleResponse();
            currentUserNewResponse.setResponse(response);
            currentUserNewResponse.setResponseTime(now());
            repositoryFacade.getScheduleRepository().updateScheduleMemberResponse(scheduleId, getCurrentUserUid(), currentUserNewResponse);
            scheduleData.setValue(scheduleData.getValue());
        }
    }

    public UserScheduleResponse getUserScheduleResponse() {
        return Objects.requireNonNull(scheduleData.getValue()).getUserScheduleResponseMap()
                .get(getCurrentUserUid());
    }

    public boolean isCurrentUserInSchedule() {
        return Objects.requireNonNull(scheduleData.getValue()).getUserScheduleResponseMap()
                .containsKey(getCurrentUserUid());
    }

    public LiveData<Schedule> getCurrentScheduleData(String scheduleId) {
        if (scheduleData == null) {
            scheduleData = new MutableLiveData<>();
            scheduleData = repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class);
        }
        return scheduleData;
    }

    public void notifySupervisorScheduleResponse() {
        ScheduleRequest newNotification = new ScheduleRequest();
        newNotification.setSenderUId(getCurrentUserUid());
        newNotification.setReceiverUId(scheduleData.getValue().getGroupSupervisorUId());
        newNotification.setGroupUId(scheduleData.getValue().getGroupUId());
        newNotification.setType("memberResponseSchedule");
        newNotification.setTimeRead(null);
        newNotification.setTimeSend(now());
        newNotification.setTime(now());
        newNotification.setSenderResponse(currentUserNewResponse);
        repositoryFacade.getNotificationRepository().addDocument(newNotification);
    }

    public boolean isOneMinuteAfterLastResponse() {
        long userLastResponseTime = getUserScheduleResponse().getResponseTime().toDate().getTime();
        return now().toDate().getTime() >= (userLastResponseTime + ONE_MINUTE);
    }
}
