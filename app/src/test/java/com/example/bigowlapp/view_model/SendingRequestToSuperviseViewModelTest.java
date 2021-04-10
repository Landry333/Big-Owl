package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SendingRequestToSuperviseViewModelTest {

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    private SendingRequestToSuperviseViewModel sendingRequestToSuperviseViewModel;

    private String userId;
    private String otherUserId;
    private String requestId;
    private SupervisionRequest supervisionRequest;

    @Before
    public void setUp() {
        userId = "userId";
        otherUserId = "otherUserId";
        requestId = "requestId";
        supervisionRequest = new SupervisionRequest();

        sendingRequestToSuperviseViewModel = new SendingRequestToSuperviseViewModel();
        sendingRequestToSuperviseViewModel.setRepositoryFacade(repositoryFacadeMock);

        when(repositoryFacadeMock.getNotificationRepository(otherUserId)).thenReturn(notificationRepositoryMock);
    }

    @Test
    public void sendSupervisionRequestToOtherUser() {
        sendingRequestToSuperviseViewModel
                .sendSupervisionRequestToOtherUser(otherUserId, supervisionRequest);
        verify(notificationRepositoryMock).addDocument(supervisionRequest);
    }

    @Test
    public void removeSupervisionRequestFromOtherUser() {
        sendingRequestToSuperviseViewModel
                .removeSupervisionRequestFromOtherUser(otherUserId, requestId);
        verify(notificationRepositoryMock).removeDocument(requestId);
    }

    @Test
    public void getRequestsDataFromOtherUser() {
        sendingRequestToSuperviseViewModel
                .getRequestsDataFromOtherUser(otherUserId, userId);
        verify(notificationRepositoryMock)
                .getListOfDocumentByAttribute(SupervisionRequest.Field.SENDER_UID, userId, SupervisionRequest.class);
    }
}