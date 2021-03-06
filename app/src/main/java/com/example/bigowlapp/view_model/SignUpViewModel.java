package com.example.bigowlapp.view_model;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

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

import java.util.ArrayList;
import java.util.List;

public class SignUpViewModel extends BaseViewModel {

    private NotificationListenerManager invitationListener;

    public SignUpViewModel() {
        // used implicitly when ViewModel constructed using ViewModelProvider
    }

    public Task<Void> createUser(String email, String password, String phoneNumber, String firstName, String lastName) {

        // Check for phone number if it's in database and unique
        Task<Void> taskIsPhoneNumberInDatabase = repositoryFacade.getUserRepository().isPhoneNumberInDatabase(phoneNumber);

        // add user email and password to authentication database
        Task<AuthResult> taskAuthSignUpResult = taskIsPhoneNumberInDatabase.onSuccessTask(task -> signUpUserInAuthRepo(email, password));

        // add basic user information to user document in firestore database
        Task<Void> taskAddUser = taskAuthSignUpResult.onSuccessTask(task -> createUserEntry(email, phoneNumber, firstName, lastName));

        // add a default group when the user registers to a system where the user is the supervisor
        return taskFinishByCreatingGroup(taskAddUser, firstName, lastName);
    }

    private Task<Void> taskFinishByCreatingGroup(Task<Void> taskPrevious, String firstName, String lastName) {
        return taskPrevious.onSuccessTask(task -> createGroupEntry(firstName, lastName));
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public Task<AuthResult> signUpUserInAuthRepo(String email, String password) {
        return repositoryFacade.getAuthRepository().signUpUser(email, password);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public Task<Void> createUserEntry(String email, String phoneNumber,
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
        repositoryFacade.getUserRepository().addDocument(uid, user);
        return Tasks.forResult(null);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public Task<Void> createGroupEntry(String firstName, String lastName) {
        Group group = new Group();
        group.setSupervisorId(getCurrentUserUid());
        group.setName(getFullName(firstName, lastName) + "'s group");
        repositoryFacade.getGroupRepository().addDocument(group);
        return Tasks.forResult(null);
    }

    public String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public void verifySmsInvitationsCollection(String phoneNumber) {
        LiveDataWithStatus<List<SmsInvitationRequest>> phoneNumberSent = repositoryFacade.getSmsInvitationRepository().getListOfDocumentByAttribute("phoneNumberSent", phoneNumber, SmsInvitationRequest.class);
        phoneNumberSent.observeForever(smsInvitationList -> {
            if (smsInvitationList == null || smsInvitationList.isEmpty()) {
                return;
            }

            for (SmsInvitationRequest smsInvitationRequest : smsInvitationList) {
                createNotificationObject(smsInvitationRequest.getSenderUid(), phoneNumber);
            }
        });
    }

    public void smsInvitationNotificationListener(Context context) {
        if (invitationListener == null) {
            invitationListener = new NotificationListenerManager(context);
        }
        invitationListener.listen();
    }

    public void createNotificationObject(String senderUid, String phoneNumber) {
        Notification newNotification = new Notification(Notification.Type.SMS_INVITATION_REQUEST);
        newNotification.setReceiverUid(senderUid);
        newNotification.setMessage("User with phone number: " + phoneNumber + " is registered.");
        newNotification.setCreationTime(Timestamp.now());

        repositoryFacade.getNotificationRepository(senderUid).addDocument(newNotification);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setInvitationListener(NotificationListenerManager invitationListener) {
        this.invitationListener = invitationListener;
    }
}
