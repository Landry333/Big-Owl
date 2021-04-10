package com.example.bigowlapp.view_model;

import android.content.Context;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.utils.NotificationListenerManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignUpViewModelTest {

    private SignUpViewModel signUpViewModel;

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private NotificationListenerManager notificationManagerMock;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<Notification> captorNotification;

    @Before
    public void setUp() {
        signUpViewModel = new SignUpViewModel();
        signUpViewModel.setInvitationListener(notificationManagerMock);
        signUpViewModel.setRepositoryFacade(repositoryFacadeMock);
    }

    @Test
    public void createUser() {
    }

    @Test
    public void getFullName() {
        String firstName = "John";
        String lastName = "Doe";
        String fullName = signUpViewModel.getFullName(firstName, lastName);
        Assert.assertEquals(firstName + " " + lastName, fullName);
    }

    @Test
    public void verifySmsInvitationsCollection() {

    }

    @Test
    public void smsInvitationNotificationListener() {
        doNothing().when(notificationManagerMock).listen(context);
        signUpViewModel.smsInvitationNotificationListener(context);
        verify(notificationManagerMock).listen(context);
    }

    @Test
    public void createNotificationObject() {
        String senderUid = "senderUid";
        String phoneNumber = "+1-555-521-5554";
        Notification newNotification = new Notification(Notification.Type.SMS_INVITATION_REQUEST);
        newNotification.setReceiverUid(senderUid);
        newNotification.setMessage("User with phone number: " + phoneNumber + " is registered.");

        when(repositoryFacadeMock.getNotificationRepository(senderUid)).thenReturn(notificationRepositoryMock);
        when(notificationRepositoryMock.addDocument(newNotification)).thenReturn(new LiveDataWithStatus<>(newNotification));

        signUpViewModel.createNotificationObject(senderUid, phoneNumber);

        verify(repositoryFacadeMock).getNotificationRepository(senderUid);
        verify(notificationRepositoryMock).addDocument(captorNotification.capture());
        Notification notificationCaptureValue = captorNotification.getValue();
        Assert.assertEquals(newNotification.getReceiverUid(), notificationCaptureValue.getReceiverUid());
        Assert.assertEquals(newNotification.getMessage(), notificationCaptureValue.getMessage());
    }
}