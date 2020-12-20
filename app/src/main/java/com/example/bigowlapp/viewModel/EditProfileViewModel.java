package com.example.bigowlapp.viewModel;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;

public class EditProfileViewModel extends BaseViewModel {

    private MutableLiveData<User> userData;

    // TODO: Dependency Injection
    public EditProfileViewModel() {
    }

    public void editUserProfile(String fName, String lName, String pNum, String imageUrl) {
        User userWithNewProfile = getCurrentUserData().getValue();
        userWithNewProfile.setFirstName(fName);
        userWithNewProfile.setLastName(lName);
        userWithNewProfile.setPhoneNumber(pNum);
        if (!imageUrl.equals(""))
            userWithNewProfile.setProfileImage(imageUrl);


        repositoryFacade.getUserRepository().updateDocument(userWithNewProfile.getUid(), userWithNewProfile);
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
        userData = repositoryFacade.getUserRepository()
                .getDocumentByUId(getCurrentUserUid(), User.class);
    }

    // TODO: Fix test that uses this method
    @VisibleForTesting
    public EditProfileViewModel(RepositoryFacade repositoryFacade, MutableLiveData<User> userData) {
        this.repositoryFacade = repositoryFacade;
        this.userData = userData;
    }
}