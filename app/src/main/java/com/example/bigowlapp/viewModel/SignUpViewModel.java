package com.example.bigowlapp.viewModel;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;

import java.security.SecureRandom;
import java.util.Locale;

public class SignUpViewModel extends BaseViewModel {

    // TODO: Dependency Injection
    public SignUpViewModel() {
    }

    // TODO: Handle exceptions concerning the failure of the "user" database collection
    public Task<Void> createUser(String email, String password, String phoneNumber, String firstName, String lastName) {
        // add user email and password to authentication database
        Task<AuthResult> taskAuthSignUpResult = repositoryFacade.getAuthRepository().signUpUser(email, password);
        // add basic user information to user document in firestore database
        Task<Void> taskAddUser = taskAuthSignUpResult.continueWithTask(task -> {
            if (task.isSuccessful()) {
                User user = new User();
                String uId = getCurrentUserUid();
                user.setUid(uId);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                repositoryFacade.getUserRepository().addDocument(uId, user);
                return Tasks.forResult(null);
            } else {
                throw task.getException();
            }
        });
        // add a default group when the user registers to a system where the user is the supervisor
        taskAddUser.addOnSuccessListener(isSuccess -> {
            Group group = new Group();
            group.setMonitoringUserId(getCurrentUserUid());
            group.setName(getFullName(firstName, lastName) + "'s group " + "#" + randomNumberStringGenerator());
            repositoryFacade.getGroupRepository().addDocument(group);
        });
        return taskAddUser;
    }

    public String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String randomNumberStringGenerator() {
        SecureRandom random = new SecureRandom();
        final int max = 9999;
        final int min = 0;
        int randomNumber = (int)((random.nextDouble() * (max - min + 1)) + min);
        return String.format(Locale.getDefault(), "%04d", randomNumber);
    }

}
