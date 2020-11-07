package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private MutableLiveData<User> user;

    // TODO: Dependency Injection
    public EditProfileViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
    }

    public void editUserProfile(String fName, String lName, String pNum, String imageUrl) {
        User userWithNewProfile = getCurrentUserProfile().getValue();
        userWithNewProfile.setFirstName(fName);
        userWithNewProfile.setLastName(lName);
        userWithNewProfile.setPhoneNumber(pNum);
        if (!imageUrl.equals(""))
            userWithNewProfile.setProfileImage(imageUrl);

        userRepository.updateDocument(userWithNewProfile.getUId(), userWithNewProfile);
        user.setValue(userWithNewProfile);
    }

    public LiveData<User> getCurrentUserProfile() {
        if (user == null) {
            user = new MutableLiveData<>();
            loadUserCurrentProfile();
        }
        return user;
    }

    private void loadUserCurrentProfile() {
        user = userRepository.getDocumentByUId(authRepository.getCurrentUser().getUid(), User.class);
    }

}