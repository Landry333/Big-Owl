package com.example.bigowlapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.UserRepository;

public class WelcomeViewModel extends ViewModel {

    private UserRepository userRepository;
    MutableLiveData<User> userLiveData;

    public WelcomeViewModel() {
        this.userRepository = new UserRepository();
    }

    //Example of a function that changes
//    public void changeUserProfileImage(String imageURL){
//        User currentUser = userLiveData.getValue();
//        currentUser.setProfileImage(imageURL);
//        User currentUserWithUpdatedPicture = currentUser;
//        userLiveData.setValue(userRepository.modifyUser(user));
//    }

    public void setUserByUId(String UId) {
        userLiveData = userRepository.getUserByUId(UId);
    }

    public void setUserByPhoneNumber(String phoneNumber) {
        userLiveData = userRepository.getUserByPhoneNumber(phoneNumber);
    }

    public LiveData<User> getUserData() {
        return userLiveData;
    }
}