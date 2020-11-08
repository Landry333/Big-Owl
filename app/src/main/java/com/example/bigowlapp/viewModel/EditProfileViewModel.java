package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.UserRepository;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private MutableLiveData<User> userData;

    // TODO: Dependency Injection
    public EditProfileViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
    }

    public void editUserProfile(String fName, String lName, String pNum, String imageUrl) {
        User userWithNewProfile = getCurrentUserData().getValue();
        userWithNewProfile.setFirstName(fName);
        userWithNewProfile.setLastName(lName);
        userWithNewProfile.setPhoneNumber(pNum);
        if (!imageUrl.equals(""))
            userWithNewProfile.setProfileImage(imageUrl);

        userRepository.updateDocument(userWithNewProfile.getUId(), userWithNewProfile);
        userData.setValue(userWithNewProfile);
    }

    public LiveData<User> getCurrentUserData() {
        if (userData == null) {
            userData = new MutableLiveData<>();
            loadUserCurrentProfile();
        }
        return userData;
    }

    private void loadUserCurrentProfile() {
        userData = userRepository.getDocumentByUId(authRepository.getCurrentUser().getUid(), User.class);
    }

    public boolean isCurrentUserSet() {
        return authRepository.getCurrentUser() != null;
    }

    @VisibleForTesting
    public EditProfileViewModel(AuthRepository authRepository, UserRepository userRepository, MutableLiveData<User> userData) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.userData = userData;
    }
}