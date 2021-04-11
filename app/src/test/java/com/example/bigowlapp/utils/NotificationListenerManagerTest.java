package com.example.bigowlapp.utils;

import android.app.NotificationManager;
import android.content.Context;

import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Notification;
import com.example.bigowlapp.model.NullNotification;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationListenerManagerTest {

    @Mock
    private RepositoryFacade repositoryFacade;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private SmsSender smsSender;
    @Mock
    private Context context;
    @Mock
    private NotificationManager notificationManager;
    @Mock
    private QuerySnapshot snapshots;
    @Mock
    private DocumentChange documentChange;
    @Mock
    private ListenerRegistration listenerRegistration;

    private NotificationListenerManager notificationListenerManager;

    @Before
    public void setUp() throws Exception {
        when(repositoryFacade.getCurrentUserNotificationRepository()).thenReturn(notificationRepository);

        notificationListenerManager = spy(new NotificationListenerManager(context));
        notificationListenerManager.setRepositoryFacade(repositoryFacade);
        notificationListenerManager.setSmsSender(smsSender);
        notificationListenerManager.setNotificationManager(notificationManager);

        doNothing().when(notificationListenerManager).sendNotification(any());
    }

    @After
    public void tearDown() {
        NotificationListenerManager.setListenerRegistration(null);
    }

    @Test
    public void stopListening() {
        assertNull(NotificationListenerManager.getListenerRegistration());
        NotificationListenerManager.stopListening();
        verify(listenerRegistration, never()).remove();

        NotificationListenerManager.setListenerRegistration(listenerRegistration);
        assertNotNull(NotificationListenerManager.getListenerRegistration());
        NotificationListenerManager.stopListening();
        verify(listenerRegistration, times(1)).remove();
        assertNull(NotificationListenerManager.getListenerRegistration());
    }

    @Test
    public void listen() {
        NotificationListenerManager.setListenerRegistration(listenerRegistration);
        notificationListenerManager.listen();
        verify(repositoryFacade, never()).getCurrentUserNotificationRepository();
    }

    @Test
    public void resolveDocumentChanges() {
        doNothing().when(notificationListenerManager).handleAuthByPhoneNumberFailure(any());
        doNothing().when(notificationListenerManager).handleNotificationRequest(any(), any());

        // empty case
        when(snapshots.getDocumentChanges()).thenReturn(new ArrayList<>());
        notificationListenerManager.resolveDocumentChanges(snapshots);
        verify(notificationListenerManager, never()).getNotificationFromDocument(any());

        when(snapshots.getDocumentChanges()).thenReturn(Collections.singletonList(documentChange));

        // invalid Notif
        when(documentChange.getType()).thenReturn(DocumentChange.Type.ADDED);

        doReturn(new NullNotification())
                .when(notificationListenerManager).getNotificationFromDocument(any());
        notificationListenerManager.resolveDocumentChanges(snapshots);
        verify(notificationListenerManager, never()).handleAuthByPhoneNumberFailure(any());
        verify(notificationListenerManager, never()).handleNotificationRequest(any(), any());

        // real Notif but Not change
        when(documentChange.getType()).thenReturn(DocumentChange.Type.REMOVED);
        doReturn(new SupervisionRequest())
                .when(notificationListenerManager).getNotificationFromDocument(any());
        notificationListenerManager.resolveDocumentChanges(snapshots);
        verify(notificationListenerManager, never()).handleAuthByPhoneNumberFailure(any());
        verify(notificationListenerManager, never()).handleNotificationRequest(any(), any());

        when(documentChange.getType()).thenReturn(DocumentChange.Type.ADDED);
        // real AuthByPhoneNumberFailure notif
        doReturn(new AuthByPhoneNumberFailure())
                .when(notificationListenerManager).getNotificationFromDocument(any());
        notificationListenerManager.resolveDocumentChanges(snapshots);
        verify(notificationListenerManager, times(1)).handleAuthByPhoneNumberFailure(any());
        verify(notificationListenerManager, never()).handleNotificationRequest(any(), any());

        // real other notif
        doReturn(new SupervisionRequest())
                .when(notificationListenerManager).getNotificationFromDocument(any());
        notificationListenerManager.resolveDocumentChanges(snapshots);
        verify(notificationListenerManager, times(1)).handleAuthByPhoneNumberFailure(any());
        verify(notificationListenerManager, times(1)).handleNotificationRequest(any(), any());
    }

    @Test
    public void handleAuthByPhoneNumberFailure() {
        AuthByPhoneNumberFailure notif = new AuthByPhoneNumberFailure();
        notif.setUid("444");
        notif.setSenderPhoneNum("+11234567890");
        notif.setScheduleId("123");

        notif.setUsed(true);
        notif.setCreationTime(veryOldTime());
        notificationListenerManager.handleAuthByPhoneNumberFailure(notif);
        verifyNoInteractions(notificationRepository);

        notif.setUsed(false);
        notif.setCreationTime(veryOldTime());
        notificationListenerManager.handleAuthByPhoneNumberFailure(notif);
        verifyNoInteractions(notificationRepository);

        notif.setUsed(true);
        notif.setCreationTime(Timestamp.now());
        notificationListenerManager.handleAuthByPhoneNumberFailure(notif);
        verifyNoInteractions(notificationRepository);

        notif.setUsed(false);
        notif.setCreationTime(Timestamp.now());
        notificationListenerManager.handleAuthByPhoneNumberFailure(notif);
        verify(smsSender).sendSMS(anyString(), anyString());
        verify(notificationRepository).updateDocument(notif.getUid(), notif);
        assertTrue(notif.isUsed());
    }

    @Test
    public void handleNotificationRequest() {
        Notification notification = new Notification(null);
        assertEquals("", notification.getTitle());

        SupervisionRequest notif = new SupervisionRequest();
        assertEquals(Notification.Type.SUPERVISION_REQUEST.title, notif.getTitle());
        notif.setUid("444");
        notif.setMessage("my message");
        notif.setUsed(true);
        notif.setTimeResponse(Timestamp.now());
        notif.setTimeRead(Timestamp.now());
        notif.setCreationTime(notif.getTimeResponse());
        notif.setGroupUid("groupId");
        notif.setSenderUid("senderId");

        notificationListenerManager.handleNotificationRequest(notif, context);
        verifyNoInteractions(notificationRepository);

        notif.setUsed(false);
        notificationListenerManager.handleNotificationRequest(notif, context);
        verify(notificationListenerManager).sendNotification(any());
        verify(notificationRepository).updateDocument(notif.getUid(), notif);
        assertEquals("groupId", notif.getGroupUid());
        assertEquals("senderId", notif.getSenderUid());
        assertNotNull(notif.getTimeRead());
    }

    private Timestamp veryOldTime() {
        return new Timestamp(Timestamp.now().getSeconds() - 40 * 60, 0);
    }

}