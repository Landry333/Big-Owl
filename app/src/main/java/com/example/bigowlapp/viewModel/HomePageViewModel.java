package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomePageViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private MutableLiveData<User> user;

    public HomePageViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
    }

    public LiveData<User> getCurrentUserData() {
        if (user == null) {
            user = new MutableLiveData<>();
            loadUserCurrentProfile();
        }
        return user;
    }

    private void loadUserCurrentProfile() {
        user = userRepository.getDocumentByUId(authRepository.getCurrentUser().getUid(), User.class);
    }

    public boolean isCurrentUserSet() {
        return authRepository.getCurrentUser() != null;
    }
}
