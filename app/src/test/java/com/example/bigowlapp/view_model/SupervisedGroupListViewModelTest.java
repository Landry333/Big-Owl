package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SupervisedGroupListViewModelTest {

    private SupervisedGroupListViewModel supervisedGroupListViewModel;

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private GroupRepository groupRepositoryMock;

    private String userUid = "userUid";

    @Before
    public void setUp() {
        supervisedGroupListViewModel = new SupervisedGroupListViewModel();
        supervisedGroupListViewModel.setRepositoryFacade(repositoryFacadeMock);
        when(repositoryFacadeMock.getGroupRepository()).thenReturn(groupRepositoryMock);
        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(userUid);
    }

    @Test
    public void getSupervisedGroupListData() {
        supervisedGroupListViewModel.getSupervisedGroupListData();
        verify(groupRepositoryMock).getListOfDocumentByArrayContains(Group.Field.MEMBER_ID_LIST,
                userUid, Group.class);
    }

    @Test
    public void getCurrentUserData() {
        supervisedGroupListViewModel.getCurrentUserData();
        verify(userRepositoryMock).getDocumentByUid(userUid, User.class);
    }
}