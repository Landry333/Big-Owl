package com.example.bigowlapp.view_model;

import androidx.lifecycle.LiveData;

import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.User;

import java.util.List;

public class NotificationActivityViewModel extends BaseViewModel {

    LiveDataWithStatus<List<Notification>> userNotificationListData;

    public LiveData<List<Notification>> getUserNotifications() {
        if (userNotificationListData == null) {
            userNotificationListData = new LiveDataWithStatus<>();
            loadUserNotifications();
        }

        return userNotificationListData;
    }

    public LiveDataWithStatus<User> getSenderUserData(String senderUid) {
        return repositoryFacade.getUserRepository().getDocumentByUid(senderUid, User.class);
    }

    public void joinGroup(Group group, String notificationUid) {
        repositoryFacade.getUserRepository().getDocumentByUid(getCurrentUserUid(), User.class).observeForever(currentUser -> {
            group.getMemberIdList().add(currentUser.getUid());
            currentUser.getMemberGroupIdList().add(group.getUid());

            repositoryFacade.getGroupRepository().updateDocument(group.getUid(), group);
            repositoryFacade.getUserRepository().updateDocument(currentUser.getUid(), currentUser);
            deleteNotification(notificationUid);
        });
    }

    public void deleteNotification(String notificationUid) {
        repositoryFacade.getCurrentUserNotificationRepository()
                .removeDocument(notificationUid);
    }

    public LiveDataWithStatus<Group> getGroupData(String supervisorUid) {
        return repositoryFacade.getGroupRepository()
                .getDocumentByAttribute(Group.Field.SUPERVISOR_ID, supervisorUid, Group.class);
    }

    private void loadUserNotifications() {
        userNotificationListData = repositoryFacade.getCurrentUserNotificationRepository()
                .getNotificationsByAscendingOrder(Notification.class);
    }
}
