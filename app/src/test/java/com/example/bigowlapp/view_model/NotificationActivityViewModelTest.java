package com.example.bigowlapp.view_model;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.UserRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationActivityViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private GroupRepository groupRepositoryMock;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    private NotificationActivityViewModel notificationActivityViewModel;

    private String senderUid;
    private String notificationUid;
    private String supervisorUid;
    private String uid;

    private Group group;
    private User user;
    private LiveDataWithStatus<User> userData;

    private String authGroupId = "authGroupId";
    private String authScheduleId = "authScheduleId";
    private String authSenderPhoneNumber = "authSenderPhone";
    private String authSenderId = "authSenderId";

    @Before
    public void setUp() {
        notificationActivityViewModel = new NotificationActivityViewModel();
        notificationActivityViewModel.setRepositoryFacade(repositoryFacadeMock);

        senderUid = "senderUid";
        notificationUid = "notificationUid";
        supervisorUid = "supervisorUid";
        uid = "UserUid";

        group = createDefaultGroup();
        user = createDefaultUser();

        userData = new LiveDataWithStatus<>(user);

        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getGroupRepository()).thenReturn(groupRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserNotificationRepository()).thenReturn(notificationRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(uid);
        when(userRepositoryMock.getDocumentByUid(anyString(), any())).thenReturn(userData);
    }

    @Test
    public void getUserNotifications() {
        LiveDataWithStatus<List<Notification>> authFailure
                = new LiveDataWithStatus<>(Collections.singletonList(createDefaultAuthNotification()));
        when(notificationRepositoryMock.getNotificationsByAscendingOrder(Notification.class)).thenReturn(authFailure);

        LiveData<List<Notification>> notificationData = notificationActivityViewModel.getUserNotifications();

        List<Notification> listOfNotification = notificationData.getValue();
        AuthByPhoneNumberFailure authResult = (AuthByPhoneNumberFailure) listOfNotification.get(0);

        verify(notificationRepositoryMock).getNotificationsByAscendingOrder(Notification.class);
        Assert.assertEquals(authGroupId, authResult.getGroupUid());
        Assert.assertEquals(authScheduleId, authResult.getScheduleId());
        Assert.assertEquals(authSenderPhoneNumber, authResult.getSenderPhoneNum());
        Assert.assertEquals(authSenderId, authResult.getSenderUid());
    }

    @Test
    public void getSenderUserData() {
        notificationActivityViewModel.getSenderUserData(senderUid);
        verify(userRepositoryMock).getDocumentByUid(senderUid, User.class);
    }

    @Test
    public void joinGroup() {
        notificationActivityViewModel.joinGroup(group, notificationUid);
        verify(groupRepositoryMock).updateDocument(group.getUid(), group);
        verify(userRepositoryMock).updateDocument(user.getUid(), user);
        verify(notificationRepositoryMock).removeDocument(notificationUid);
    }

    @Test
    public void getGroupData() {
        notificationActivityViewModel.getGroupData(supervisorUid);
        verify(groupRepositoryMock)
                .getDocumentByAttribute(Group.Field.SUPERVISOR_ID, supervisorUid, Group.class);
    }

    private AuthByPhoneNumberFailure createDefaultAuthNotification() {
        AuthByPhoneNumberFailure authByPhoneNumberFailure = new AuthByPhoneNumberFailure();
        authByPhoneNumberFailure.setGroupUid(authGroupId);
        authByPhoneNumberFailure.setScheduleId(authScheduleId);
        authByPhoneNumberFailure.setSenderPhoneNum(authSenderPhoneNumber);
        authByPhoneNumberFailure.setSenderUid(authSenderId);
        return authByPhoneNumberFailure;
    }

    private Group createDefaultGroup() {
        Group newGroup = new Group();
        newGroup.setUid("groupUid");
        newGroup.setName("123");
        newGroup.setSupervisorId(supervisorUid);
        newGroup.setMemberIdList(new ArrayList<>());
        return newGroup;
    }

    private User createDefaultUser() {
        uid = "UserUid";
        String email = "abc@email.com";
        String phoneNumber = "+1-555-521-5554";
        String firstName = "Joe";
        String lastName = "Doe";

        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMemberGroupIdList(new ArrayList<>());
        user.setProfileImage("");
        return user;
    }
}