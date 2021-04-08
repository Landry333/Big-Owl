package com.example.bigowlapp.view_model;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;

public class HomePageViewModel extends BaseViewModel {

    private LiveDataWithStatus<User> user;

    public HomePageViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public LiveDataWithStatus<User> getCurrentUserData() {
        if (user == null) {
            user = new LiveDataWithStatus<>();
            loadUserCurrentProfile();
        }
        return user;
    }

    public void updateUser(User user){
        repositoryFacade.getUserRepository().updateDocument(user.getUid(), user);
    }

    private void loadUserCurrentProfile() {
        user = repositoryFacade.getUserRepository()
                .getDocumentByUid(getCurrentUserUid(), User.class);
    }

    public void signOut() {
        repositoryFacade.getAuthRepository().signOutUser();
    }
}
