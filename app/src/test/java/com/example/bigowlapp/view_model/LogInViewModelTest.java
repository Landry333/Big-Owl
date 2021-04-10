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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogInViewModelTest {

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private AuthRepository authRepositoryMock;

    private LogInViewModel logInViewModel;

    private String userId;
    private String email;
    private String password;

    @Before
    public void setUp() {
        userId = "userId";
        email = "abc@email.com";
        password = "123456";
        logInViewModel = new LogInViewModel();
        logInViewModel.setRepositoryFacade(repositoryFacadeMock);
        when(repositoryFacadeMock.getAuthRepository()).thenReturn(authRepositoryMock);
        when(repositoryFacadeMock.getUserRepository()).thenReturn(userRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(userId);
    }

    @Test
    public void logInUser() {
        logInViewModel.logInUser(email, password);
        verify(authRepositoryMock).signInUser(email, password);
    }

    @Test
    public void getCurrentUserData() {
        logInViewModel.getCurrentUserData();
        verify(userRepositoryMock).getDocumentByUid(userId, User.class);
    }
}