package com.example.bigowlapp.view_model;

import androidx.lifecycle.LiveData;

import com.example.bigowlapp.model.SupervisionRequest;

import java.util.List;

public class SendingRequestToSuperviseViewModel extends BaseViewModel {

    public SendingRequestToSuperviseViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public void sendSupervisionRequestToOtherUser(String otherUserId, SupervisionRequest supervisionRequest) {
        repositoryFacade.getNotificationRepository(otherUserId).addDocument(supervisionRequest);
    }

    public void removeSupervisionRequestFromOtherUser(String otherUserId, String requestUid) {
        repositoryFacade.getNotificationRepository(otherUserId).removeDocument(requestUid);
    }

    public LiveData<List<SupervisionRequest>> getRequestsDataFromOtherUser(String otherUserId, String currentUserId) {
        return repositoryFacade.getNotificationRepository(otherUserId)
                .getListOfDocumentByAttribute(SupervisionRequest.Field.SENDER_UID, currentUserId, SupervisionRequest.class);
    }

}
