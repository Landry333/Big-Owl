package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationActivityViewModelTest {

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
        when(userRepositoryMock.getDocumentByUid(uid, User.class)).thenReturn(userData);
    }

    @Test
    public void getUserNotifications() {
        notificationActivityViewModel.getUserNotifications();
        verify(notificationRepositoryMock).getNotificationsByAscendingOrder(Notification.class);
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