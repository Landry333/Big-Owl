package com.example.bigowlapp.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.exception.DocumentNotFoundException;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTest {

    @Mock
    private FirebaseFirestore dbMock;
    @Mock
    private CollectionReference collectionReferenceMock;
    @Mock
    private DocumentReference docRefMock;
    @Mock
    private QuerySnapshot querySnapshotMock;
    @Mock
    private DocumentSnapshot docSnapshotMock;
    @Mock
    private Query queryMock;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ScheduleRepository repository;
    private Schedule data;
    private String uid;

    @Before
    public void setUp() throws Exception {
        when(queryMock.get()).thenReturn(Tasks.forResult(querySnapshotMock));

        when(docRefMock.get()).thenReturn(Tasks.forResult(docSnapshotMock));
        when(collectionReferenceMock.document(anyString())).thenReturn(docRefMock);

        when(collectionReferenceMock.add(any())).thenReturn(Tasks.forResult(docRefMock));
        when(collectionReferenceMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);

        repository = new ScheduleRepository(dbMock, collectionReferenceMock);
        data = Schedule.getPrototypeSchedule();
        uid = "uid";

    }

    @Test
    public void resolveTaskForUpdateResult() {
        LiveDataWithStatus<Schedule> dataOneLiveData = new LiveDataWithStatus<>();

        repository.resolveTaskForUpdateResult(Tasks.forResult("val"), dataOneLiveData, new Schedule());
        assertEquals(LiveDataWithStatus.Status.SUCCESS, dataOneLiveData.getStatus());
        assertFalse(dataOneLiveData.hasError());
        assertTrue(dataOneLiveData.isComplete());
        assertNotNull(dataOneLiveData.getValue());

        repository.resolveTaskForUpdateResult(Tasks.forException(new Exception()), dataOneLiveData, new Schedule());
        assertEquals(LiveDataWithStatus.Status.ERROR, dataOneLiveData.getStatus());
        assertTrue(dataOneLiveData.hasError());
        assertTrue(dataOneLiveData.isComplete());
        assertNull(dataOneLiveData.getValue());
        assertTrue(dataOneLiveData.getError() instanceof Exception);
    }

    @Test
    public void resolveTaskWithListResult() {
        List<Schedule> dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dataList.add(Schedule.getPrototypeSchedule());
        }

        LiveDataWithStatus<List<Schedule>> dataListLiveData = new LiveDataWithStatus<>();

        // null case
        repository.resolveTaskWithListResult(Tasks.forResult(null), dataListLiveData, Schedule.class);
        assertEquals(LiveDataWithStatus.Status.ERROR, dataListLiveData.getStatus());
        assertTrue(dataListLiveData.hasError());
        assertTrue(dataListLiveData.isComplete());
        assertNull(dataListLiveData.getValue());
        assertTrue(dataListLiveData.getError() instanceof DocumentNotFoundException);

        // empty case
        when(querySnapshotMock.isEmpty()).thenReturn(true);
        repository.resolveTaskWithListResult(Tasks.forResult(querySnapshotMock), dataListLiveData, Schedule.class);
        assertEquals(LiveDataWithStatus.Status.ERROR, dataListLiveData.getStatus());
        assertTrue(dataListLiveData.hasError());
        assertTrue(dataListLiveData.isComplete());
        assertNull(dataListLiveData.getValue());
        assertTrue(dataListLiveData.getError() instanceof DocumentNotFoundException);

        // fail case
        repository.resolveTaskWithListResult(Tasks.forException(new Exception()), dataListLiveData, Schedule.class);
        assertEquals(LiveDataWithStatus.Status.ERROR, dataListLiveData.getStatus());
        assertTrue(dataListLiveData.hasError());
        assertTrue(dataListLiveData.isComplete());
        assertNull(dataListLiveData.getValue());
        assertTrue(dataListLiveData.getError() instanceof Exception);

        // success case
        List<QueryDocumentSnapshot> queryMockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            QueryDocumentSnapshot mockQueryDoc = mock(QueryDocumentSnapshot.class);
            when(mockQueryDoc.toObject(Schedule.class)).thenReturn(dataList.get(i));
            queryMockList.add(mockQueryDoc);
        }

        when(querySnapshotMock.isEmpty()).thenReturn(false);
        when(querySnapshotMock.iterator()).thenReturn(queryMockList.iterator());

        repository.resolveTaskWithListResult(Tasks.forResult(querySnapshotMock), dataListLiveData, Schedule.class);
        assertEquals(LiveDataWithStatus.Status.SUCCESS, dataListLiveData.getStatus());
        assertFalse(dataListLiveData.hasError());
        assertTrue(dataListLiveData.isComplete());
        assertArrayEquals(dataList.toArray(), dataListLiveData.getValue().toArray());
    }

    @Test
    public void listenToCollection() {
        EventListener<QuerySnapshot> listenerToAdd = (value, error) -> {
        };
        repository.listenToCollection(listenerToAdd);
        verify(collectionReferenceMock).addSnapshotListener(listenerToAdd);
    }

    @Test
    public void addDocument() {
        assertNull(data.getUid());

        // success result
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.addDocument(data);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());

        // failure result
        when(collectionReferenceMock.add(any())).thenReturn(Tasks.forException(new Exception()));
        LiveDataWithStatus<Schedule> resultDataFail = repository.addDocument(data);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataFail.isComplete());
    }

    @Test
    public void addDocumentWithManualId() {
        when(docRefMock.set(any(data.getClass()))).thenReturn(Tasks.forResult(null));
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.addDocument(uid, data);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }

    @Test
    public void removeDocument() {
        when(docRefMock.delete()).thenReturn(Tasks.forResult(null));
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.removeDocument(uid);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }

    @Test
    public void updateDocument() {
        when(docRefMock.set(any(), eq(SetOptions.merge()))).thenReturn(Tasks.forResult(null));
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.updateDocument(uid, data);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }

    @Test
    public void getDocumentByUid() {
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.getDocumentByUid(uid, Schedule.class);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }

    @Test
    public void getDocumentByAttribute() {
        when(queryMock.limit(1)).thenReturn(queryMock);
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.getDocumentByAttribute(Schedule.Field.EVENT, "event", Schedule.class);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }

    @Test
    public void getListOfDocumentByAttribute() {
        LiveDataWithStatus<List<Schedule>> resultDataSuccess = repository.getListOfDocumentByAttribute(Schedule.Field.EVENT, "event", Schedule.class);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }

    @Test
    public void getListOfDocumentByArrayContains() {
        when(collectionReferenceMock.whereArrayContains(anyString(), anyString())).thenReturn(queryMock);
        LiveDataWithStatus<List<Schedule>> resultDataSuccess = repository.getListOfDocumentByArrayContains(Schedule.Field.EVENT, "event", Schedule.class);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());

        verify(collectionReferenceMock).whereArrayContains(Schedule.Field.EVENT, "event");
    }

    @Test
    public void getDocumentsByListOfUid() {
        when(collectionReferenceMock.whereIn(eq(FieldPath.documentId()), anyList())).thenReturn(queryMock);
        LiveDataWithStatus<List<Schedule>> resultDataSuccess = repository.getDocumentsByListOfUid(Collections.singletonList("id1"), Schedule.class);
        assertEquals(LiveDataWithStatus.Status.NONE, resultDataSuccess.getStatus());
        assertFalse(resultDataSuccess.isComplete());
        assertNull(resultDataSuccess.getValue());
    }
}