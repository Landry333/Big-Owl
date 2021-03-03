package com.example.bigowlapp.viewModel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditProfileViewModelTest {

    private EditProfileViewModel editProfileViewModel;
    private LiveDataWithStatus<User> testUserData;
    private User testUser;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RepositoryFacade repositoryFacade;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private FirebaseUser testFirebaseUser;

    @Before
    public void setUp() {
        when(repositoryFacade.getAuthRepository()).thenReturn(authRepository);
        when(repositoryFacade.getUserRepository()).thenReturn(userRepository);

        testUser = new User("abc123", "first", "last", "+911", "test@mail.com", "url", null);
        testUserData = new LiveDataWithStatus<>(testUser);

        when(authRepository.getCurrentUser()).thenReturn(testFirebaseUser);
        when(userRepository.getDocumentByUid(anyString(), eq(User.class))).thenReturn(testUserData);
        when(testFirebaseUser.getUid()).thenReturn("abc123");

        editProfileViewModel = new EditProfileViewModel(repositoryFacade, testUserData);
    }

    @Test
    public void editUserProfileTest() {
        editProfileViewModel.editUserProfile("1st", "2nd", "+123", "newUrl");
        verify(userRepository).updateDocument("abc123", testUser);
        assertEquals("1st", testUser.getFirstName());
        assertEquals("2nd", testUser.getLastName());
        assertEquals("+123", testUser.getPhoneNumber());
        assertEquals("newUrl", testUser.getProfileImage());

        editProfileViewModel.editUserProfile("1st", "2nd", "+123", "");
        verify(userRepository, times(2)).updateDocument("abc123", testUser);
        assertEquals("newUrl", testUser.getProfileImage());
    }

    @Test
    public void getCurrentUserDataTest() {
        editProfileViewModel = new EditProfileViewModel(repositoryFacade, null);
        LiveData<User> target = editProfileViewModel.getCurrentUserData();
        verify(userRepository).getDocumentByUid(anyString(), eq(User.class));
        assertEquals(testUserData, target);

        LiveData<User> target2 = editProfileViewModel.getCurrentUserData();
        verify(userRepository, times(1)).getDocumentByUid(anyString(), eq(User.class));
        assertEquals(testUserData, target2);
    }

    @Test
    public void isCurrentUserSetTest() {
        boolean target = editProfileViewModel.isCurrentUserSet();
        verify(authRepository).getCurrentUser();
        assertTrue(target);

        when(authRepository.getCurrentUser()).thenReturn(null);
        target = editProfileViewModel.isCurrentUserSet();
        assertFalse(target);
    }
}