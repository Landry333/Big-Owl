package com.example.bigowlapp.view_model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SmsInvitationRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.SmsInvitationRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.example.bigowlapp.utils.NotificationListenerManager;
import com.google.android.gms.tasks.Tasks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignUpViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SignUpViewModel signUpViewModel;

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private NotificationListenerManager notificationManagerMock;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    @Mock
    private SmsInvitationRepository smsInvitationRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private AuthRepository authRepositoryMock;

    @Mock
    private GroupRepository groupRepositoryMock;

    @Mock
    private Context context;

    @Mock
    private List<SmsInvitationRequest> listOfSmsRequestMock;

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
        String uid = "uid";
        String email = "abc@email.com";
        String password = "123456";
        String phoneNumber = "+1-555-521-5554";
        String firstName = "Joe";
        String lastName = "Doe";

        User user = createDefaultUser();
        Group group = createDefaultGroup();

        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(uid);
        when(userRepositoryMock.isPhoneNumberInDatabase(phoneNumber))
                .thenReturn(Tasks.forResult(null));
        when(repositoryFacadeMock.getAuthRepository()).thenReturn(authRepositoryMock);
        when(authRepositoryMock.signUpUser(email, password)).thenReturn(Tasks.forResult(null));
        when(userRepositoryMock.addDocument(uid, user)).thenReturn(null);
        when(repositoryFacadeMock.getGroupRepository()).thenReturn(groupRepositoryMock);
        when(groupRepositoryMock.addDocument(group)).thenReturn(null);


        signUpViewModel.createUser(email, password, phoneNumber, firstName, lastName);
    }

    private Group createDefaultGroup() {
        String uid = "uid";
        String firstName = "Joe";
        String lastName = "Doe";
        Group group = new Group();
        group.setSupervisorId(uid);
        group.setName(firstName + " " + lastName + "'s group");
        return group;
    }

    private User createDefaultUser() {
        String uid = "uid";
        String email = "abc@email.com";
        String phoneNumber = "+1-555-521-5554";
        String firstName = "Joe";
        String lastName = "Doe";

        User user = new User();;
        user.setUid(uid);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMemberGroupIdList(new ArrayList<>());
        user.setProfileImage("");
        return user;
    }

    @Test
    public void getFullName() {
        String firstName = "John";
        String lastName = "Doe";
        String fullName = signUpViewModel.getFullName(firstName, lastName);
        Assert.assertEquals(firstName + " " + lastName, fullName);
    }

    @Test
    public void verifySmsInvitationsCollectionEmptyList() {
        String phoneNumber = "+1-555-521-5554";
        when(repositoryFacadeMock.getSmsInvitationRepository()).thenReturn(smsInvitationRepositoryMock);
        when(smsInvitationRepositoryMock
                .getListOfDocumentByAttribute("phoneNumberSent", phoneNumber, SmsInvitationRequest.class))
                .thenReturn(new LiveDataWithStatus<>(listOfSmsRequestMock));
        when(listOfSmsRequestMock.isEmpty()).thenReturn(true);

        signUpViewModel.verifySmsInvitationsCollection(phoneNumber);

        verify(smsInvitationRepositoryMock)
                .getListOfDocumentByAttribute("phoneNumberSent", phoneNumber, SmsInvitationRequest.class);
        verify(listOfSmsRequestMock).isEmpty();
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