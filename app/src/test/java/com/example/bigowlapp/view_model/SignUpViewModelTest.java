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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
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

    @Captor
    private ArgumentCaptor<User> captorUser;

    @Captor
    private ArgumentCaptor<Group> captorGroup;

    private String uid;
    private String email;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    @Before
    public void setUp() {
        signUpViewModel = new SignUpViewModel();
        signUpViewModel.setInvitationListener(notificationManagerMock);
        signUpViewModel.setRepositoryFacade(repositoryFacadeMock);

        uid = "uid";
        email = "abc@email.com";
        password = "123456";
        phoneNumber = "+1-555-521-5554";
        firstName = "Joe";
        lastName = "Doe";
    }

    @Test
    public void createUser() {
        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(userRepositoryMock.isPhoneNumberInDatabase(phoneNumber))
                .thenReturn(Tasks.forResult(null));
        signUpViewModel.createUser(email, password, phoneNumber, firstName, lastName);
        verify(repositoryFacadeMock).getUserRepository();
        verify(userRepositoryMock).isPhoneNumberInDatabase(phoneNumber);
    }

    @Test
    public void signUpInAuthRepo() {
        when(repositoryFacadeMock.getAuthRepository()).thenReturn(authRepositoryMock);
        when(authRepositoryMock.signUpUser(email, password)).thenReturn(Tasks.forResult(null));
        signUpViewModel.signUpUserInAuthRepo(email, password);
        verify(repositoryFacadeMock).getAuthRepository();
        verify(authRepositoryMock).signUpUser(email, password);
    }

    @Test
    public void createUserEntry() {
        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(uid);
        signUpViewModel.createUserEntry(email, phoneNumber, firstName, lastName);
        verify(userRepositoryMock).addDocument(anyString(), captorUser.capture());

        User userCaptureValue = captorUser.getValue();

        Assert.assertEquals(uid, userCaptureValue.getUid());
        Assert.assertEquals(email, userCaptureValue.getEmail());
        Assert.assertEquals(phoneNumber, userCaptureValue.getPhoneNumber());
        Assert.assertEquals(firstName, userCaptureValue.getFirstName());
        Assert.assertEquals(lastName, userCaptureValue.getLastName());
    }

    @Test
    public void createGroupEntry() {
        when(repositoryFacadeMock.getGroupRepository()).thenReturn(groupRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(uid);
        signUpViewModel.createGroupEntry(firstName, lastName);
        verify(groupRepositoryMock).addDocument(captorGroup.capture());

        Group group = captorGroup.getValue();

        Assert.assertEquals(firstName + " " + lastName + "'s group", group.getName());
        Assert.assertEquals(uid, group.getSupervisorId());
    }

    @Test
    public void getFullName() {
        String fullName = signUpViewModel.getFullName(firstName, lastName);
        Assert.assertEquals(firstName + " " + lastName, fullName);
    }

    @Test
    public void verifySmsInvitationsCollectionEmptyList() {
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
        doNothing().when(notificationManagerMock).listen();
        signUpViewModel.smsInvitationNotificationListener(context);
        verify(notificationManagerMock).listen();
    }

    @Test
    public void createNotificationObject() {
        String senderUid = "senderUid";
        Notification newNotification = new Notification(Notification.Type.SMS_INVITATION_REQUEST);
        newNotification.setReceiverUid(senderUid);
        newNotification.setMessage("User with phone number: " + phoneNumber + " is registered.");

        when(repositoryFacadeMock.getNotificationRepository(senderUid)).thenReturn(notificationRepositoryMock);

        signUpViewModel.createNotificationObject(senderUid, phoneNumber);

        verify(repositoryFacadeMock).getNotificationRepository(senderUid);
        verify(notificationRepositoryMock).addDocument(captorNotification.capture());
        Notification notificationCaptureValue = captorNotification.getValue();
        Assert.assertEquals(newNotification.getReceiverUid(), notificationCaptureValue.getReceiverUid());
        Assert.assertEquals(newNotification.getMessage(), notificationCaptureValue.getMessage());
    }
}