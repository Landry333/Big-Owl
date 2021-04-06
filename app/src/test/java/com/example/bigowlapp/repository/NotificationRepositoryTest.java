package com.example.bigowlapp.repository;

import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.NullNotification;
import com.example.bigowlapp.model.ScheduleRequest;
import com.example.bigowlapp.model.SupervisionRequest;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationRepositoryTest {
    @Mock
    DocumentSnapshot doc;

    @Before
    public void setUp() {
        when(doc.toObject(SupervisionRequest.class)).thenReturn(new SupervisionRequest());
        when(doc.toObject(ScheduleRequest.class)).thenReturn(new ScheduleRequest());
        when(doc.toObject(NullNotification.class)).thenReturn(new NullNotification());
    }

    @Test
    public void getNotificationFromDocument() {
        Notification notificationFromDoc;

        // null cases
        when(doc.toObject(Notification.class)).thenReturn(null);
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, Notification.class);
        assertNotNull(notificationFromDoc);
        assertEquals(NullNotification.class, notificationFromDoc.getClass());

        when(doc.toObject(Notification.class)).thenReturn(new Notification(null));
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, Notification.class);
        assertNotNull(notificationFromDoc);
        assertEquals(NullNotification.class, notificationFromDoc.getClass());


        // case where ask for any Notification type
        when(doc.toObject(Notification.class)).thenReturn(new Notification());
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, Notification.class);
        assertEquals(Notification.class, notificationFromDoc.getClass());

        when(doc.toObject(Notification.class)).thenReturn(new Notification(Notification.Type.SUPERVISION_REQUEST));
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, Notification.class);
        assertEquals(SupervisionRequest.class, notificationFromDoc.getClass());

        when(doc.toObject(Notification.class)).thenReturn(new Notification(Notification.Type.SCHEDULE_REQUEST));
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, Notification.class);
        assertEquals(ScheduleRequest.class, notificationFromDoc.getClass());

        // case where ask for Specific Notification type
        when(doc.toObject(Notification.class)).thenReturn(new Notification());
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, SupervisionRequest.class);
        assertEquals(NullNotification.class, notificationFromDoc.getClass());

        when(doc.toObject(Notification.class)).thenReturn(new Notification(Notification.Type.SUPERVISION_REQUEST));
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, SupervisionRequest.class);
        assertEquals(SupervisionRequest.class, notificationFromDoc.getClass());

        when(doc.toObject(Notification.class)).thenReturn(new Notification(Notification.Type.SCHEDULE_REQUEST));
        notificationFromDoc = NotificationRepository.getNotificationFromDocument(doc, SupervisionRequest.class);
        assertEquals(NullNotification.class, notificationFromDoc.getClass());

    }
}