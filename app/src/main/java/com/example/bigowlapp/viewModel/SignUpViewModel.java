package com.example.bigowlapp.viewModel;

import android.content.Context;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SmsInvitationRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.NotificationListenerManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;

import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class SignUpViewModel extends BaseViewModel {

    // TODO: Dependency Injection
    public SignUpViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    // TODO: Handle exceptions concerning the failure of the "user" database collection
    public Task<Void> createUser(String email, String password, String phoneNumber, String firstName, String lastName) {

        // Check for phone number if it's in database and unique
        Task<Void> taskIsPhoneNumberInDatabase =
                repositoryFacade.getUserRepository().isPhoneNumberInDatabase(phoneNumber);

        // add user email and password to authentication database
        Task<AuthResult> taskAuthSignUpResult = taskIsPhoneNumberInDatabase.continueWithTask(task -> {
            if (task.isSuccessful()) {
                return signUpUserInAuthRepo(email, password);
            } else {
                throw task.getException();
            }
        });

        // add basic user information to user document in firestore database
        Task<Void> taskAddUser = taskAuthSignUpResult.continueWithTask(task -> {
            if (task.isSuccessful()) {
                return createUserEntry(email, phoneNumber, firstName, lastName);
            } else {
                throw task.getException();
            }
        });

        // add a default group when the user registers to a system where the user is the supervisor
        return taskFinishByCreatingGroup(taskAddUser, firstName, lastName);
    }

    private Task<Void> taskFinishByCreatingGroup(Task<Void> taskPrevious, String firstName, String lastName) {
        return taskPrevious.continueWithTask(task -> {
            if (task.isSuccessful()) {
                return createGroupEntry(firstName, lastName);
            } else {
                throw task.getException();
            }
        });
    }

    private Task<AuthResult> signUpUserInAuthRepo(String email, String password) {
        return repositoryFacade.getAuthRepository().signUpUser(email, password);
    }

    private Task<Void> createUserEntry(String email, String phoneNumber,
                                       String firstName, String lastName) {
        User user = new User();
        String uid = getCurrentUserUid();
        user.setUid(uid);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMemberGroupIdList(new ArrayList<>());
        user.setProfileImage("");
        // TODO: Can't capture error b/c it's a livedata call; need to change repo structure
        repositoryFacade.getUserRepository().addDocument(uid, user);
        return Tasks.forResult(null);
    }

    private Task<Void> createGroupEntry(String firstName, String lastName) {
        Group group = new Group();
        group.setSupervisorId(getCurrentUserUid());
        group.setName(getFullName(firstName, lastName) + "'s group " + "#" + randomNumberStringGenerator());
        // TODO: Can't capture error b/c it's a livedata call; need to change repo structure
        repositoryFacade.getGroupRepository().addDocument(group);
        return Tasks.forResult(null);
    }

    public String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public String randomNumberStringGenerator() {
        SecureRandom random = new SecureRandom();
        final int max = 9999;
        final int min = 0;
        int randomNumber = (int) ((random.nextDouble() * (max - min + 1)) + min);
        return String.format(Locale.getDefault(), "%04d", randomNumber);
    }

    public void verifySmsInvitationsCollection(String phoneNumber){
        LiveDataWithStatus<List<SmsInvitationRequest>> phoneNumberSent = repositoryFacade.getSmsInvitationRepository().getListOfDocumentByAttribute("phoneNumberSent", phoneNumber, SmsInvitationRequest.class);
        phoneNumberSent.observeForever(smsInvitationList -> {
            if(smsInvitationList.isEmpty()){
                return;
            }

            for (SmsInvitationRequest smsInvitationRequest : smsInvitationList) {
                createNotificationObject(smsInvitationRequest.getSenderUid(), phoneNumber);
            }
        });
    }

    public void smsInvitationNotificationListener(Context context){
        NotificationListenerManager invitationListener = new NotificationListenerManager();
        invitationListener.listen(context);
    }

    public void createNotificationObject(String senderUid, String phoneNumber){

        Notification newNotification = new Notification();
        newNotification.setReceiverUid(senderUid);
        newNotification.setMessage("User with phone number: " + phoneNumber + " is registered.");
        newNotification.setCreationTime(Timestamp.now());

        repositoryFacade.getNotificationRepository(senderUid).addDocument(newNotification);

    }
}
