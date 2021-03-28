package com.example.bigowlapp.model;

import com.google.firebase.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTest {

    @Test
    public void getNotificationSafe() {
        Notification notificationSafeResult = Notification.getNotificationSafe(null);
        assertNotNull(notificationSafeResult);
        assertEquals(NullNotification.class, notificationSafeResult.getClass());

        notificationSafeResult = Notification.getNotificationSafe(new Notification(null));
        assertNotNull(notificationSafeResult);
        assertEquals(NullNotification.class, notificationSafeResult.getClass());

        Notification validNotif = new Notification();
        notificationSafeResult = Notification.getNotificationSafe(validNotif);
        assertEquals(validNotif, notificationSafeResult);

        validNotif = new SupervisionRequest();
        notificationSafeResult = Notification.getNotificationSafe(validNotif);
        assertEquals(validNotif, notificationSafeResult);
    }

    @Test
    public void isValid() {
        Notification notification = new Notification();
        assertTrue(notification.isValid());

        notification = new Notification(null);
        assertTrue(notification.isValid());

        notification = new ScheduleRequest();
        assertTrue(notification.isValid());

        notification = new NullNotification();
        assertFalse(notification.isValid());
    }

    @Test
    public void timeSinceCreationMillis() {
        Notification notification = new Notification();

        notification.setCreationTime(Timestamp.now());
        assertTrue(notification.timeSinceCreationMillis() > 0);

        notification.setCreationTime(null);
        assertEquals(-1L, notification.timeSinceCreationMillis());
    }
}