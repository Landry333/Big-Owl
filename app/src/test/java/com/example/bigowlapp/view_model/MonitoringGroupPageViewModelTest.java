package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.UserRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringGroupPageViewModelTest {

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private GroupRepository groupRepositoryMock;

    private MonitoringGroupPageViewModel monitoringGroupPageViewModel;

    private String userUid = "UserUid";
    private String groupUid = "GroupUid";

    @Before
    public void setUp() {
        monitoringGroupPageViewModel = new MonitoringGroupPageViewModel();
        monitoringGroupPageViewModel.setRepositoryFacade(repositoryFacadeMock);
        when(repositoryFacadeMock.getGroupRepository()).thenReturn(groupRepositoryMock);
        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(userUid);
    }

    @Test
    public void getGroup() {
        monitoringGroupPageViewModel.getGroup();
        verify(groupRepositoryMock).getDocumentByAttribute(Group.Field.SUPERVISOR_ID, userUid, Group.class);
    }

    @Test
    public void getUsersFromGroup() {
        Group group = new Group();
        group.setUid(groupUid);
        group.setName("123");
        group.setSupervisorId("otherUserUid");
        group.setMemberIdList(new ArrayList<>());

        monitoringGroupPageViewModel.getUsersFromGroup(group);
        verify(userRepositoryMock)
                .getListOfDocumentByArrayContains(User.Field.MEMBER_GROUP_ID_LIST, group.getUid(), User.class);
    }

    @Test
    public void removeUserFromGroup() {
        User userToRemove = createDefaultUser();
        ArrayList<String> listOfGroups = new ArrayList<>();
        listOfGroups.add(groupUid);
        userToRemove.setMemberGroupIdList(listOfGroups);

        Group group = new Group();
        String otherUserId = "otherUserUid";
        group.setUid(groupUid);
        group.setName("123");
        group.setSupervisorId(otherUserId);
        ArrayList<String> listOfMembers = new ArrayList<>();
        listOfMembers.add(userToRemove.getUid());
        group.setMemberIdList(listOfMembers);

        when(groupRepositoryMock.getDocumentByAttribute(Group.Field.SUPERVISOR_ID, userToRemove.getUid(), Group.class))
                .thenReturn(new LiveDataWithStatus<>(group));

        monitoringGroupPageViewModel.removeUserFromGroup(userToRemove);

        // The userUid and GroupUid should be removed in both lists
        Assert.assertTrue(userToRemove.getMemberGroupIdList().isEmpty());
        Assert.assertTrue(group.getMemberIdList().isEmpty());
    }

    private User createDefaultUser() {
        String uid = userUid;
        String email = "abc@email.com";
        String phoneNumber = "+1-555-521-5554";
        String firstName = "Joe";
        String lastName = "Doe";

        User user = new User();
        ;
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