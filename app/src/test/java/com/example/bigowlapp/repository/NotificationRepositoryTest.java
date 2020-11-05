package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.utils.Constants;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationRepositoryTest {

    private NotificationRepository notificationRepository;

    @Before
    public void setUp() {
        notificationRepository = new NotificationRepository(null, null);
    }

    @Test
    public void getClassFromType() {
        String nullType = null;
        assertEquals(Notification.class, notificationRepository.getClassFromType(nullType));
        String emptyType = "";
        assertEquals(Notification.class, notificationRepository.getClassFromType(emptyType));
        String randomType = "someRandomString";
        assertEquals(Notification.class, notificationRepository.getClassFromType(randomType));

        String reservedSupervisionNotificationType = Constants.SUPERVISION_TYPE;
        assertEquals(SupervisionRequest.class, notificationRepository.getClassFromType(reservedSupervisionNotificationType));
    }
}