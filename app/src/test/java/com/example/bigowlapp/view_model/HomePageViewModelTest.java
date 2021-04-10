package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
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
public class HomePageViewModelTest {

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private AuthRepository authRepositoryMock;

    private HomePageViewModel homePageViewModel;

    private String uid;

    @Before
    public void setUp() {
        uid = "UserUid";
        homePageViewModel = new HomePageViewModel();
        homePageViewModel.setRepositoryFacade(repositoryFacadeMock);
        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getAuthRepository()).thenReturn(authRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(uid);
    }

    @Test
    public void getCurrentUserData() {
        homePageViewModel.getCurrentUserData();
        verify(userRepositoryMock).getDocumentByUid(uid, User.class);
    }

    @Test
    public void updateUser() {
        User user = createDefaultUser();
        homePageViewModel.updateUser(user);
        verify(userRepositoryMock).updateDocument(user.getUid(), user);
    }

    @Test
    public void signOut() {
        homePageViewModel.signOut();
        verify(authRepositoryMock).signOutUser();
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