package com.example.bigowlapp.viewModel;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Response;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.ScheduleRequest;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;

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
        currentUserNewResponse = getUserScheduleResponse();
        currentUserNewResponse.setResponse(response);
        currentUserNewResponse.setResponseTime(now());
        repositoryFacade.getScheduleRepository().updateScheduleMemberResponse(scheduleId, getCurrentUserUid(), currentUserNewResponse);
        scheduleData.setValue(scheduleData.getValue());

        notifySupervisorScheduleResponse();
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
        String supervisorUid = scheduleData.getValue().getGroupSupervisorUid();

        ScheduleRequest newNotification = new ScheduleRequest();
        newNotification.setSenderUid(getCurrentUserUid());
        newNotification.setReceiverUid(supervisorUid);
        newNotification.setGroupUid(scheduleData.getValue().getGroupUid());
        newNotification.setTimeRead(null);
        newNotification.setTime(now());
        newNotification.setSenderResponse(currentUserNewResponse);
        repositoryFacade.getNotificationRepository(supervisorUid)
                .addDocument(newNotification);
    }

    public boolean isOneMinuteAfterLastResponse() {
        Timestamp responseTimestamp = getUserScheduleResponse().getResponseTime();
        // If there was no response yet, then we should allow response to go through
        if (responseTimestamp == null) {
            return true;
        }
        long userLastResponseTime = responseTimestamp.toDate().getTime();
        return now().toDate().getTime() >= (userLastResponseTime + ONE_MINUTE);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public UserScheduleResponse getCurrentUserNewResponse() {
        return currentUserNewResponse;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setScheduleData(MutableLiveData<Schedule> scheduleData) {
        this.scheduleData = scheduleData;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public ScheduleViewRespondViewModel(RepositoryFacade repositoryFacade) {
        this.repositoryFacade = repositoryFacade;
    }
}
