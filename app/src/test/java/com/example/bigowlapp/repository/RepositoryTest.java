package com.example.bigowlapp.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.repository.exception.DocumentNotFoundException;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    private QuerySnapshot queryMock;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ScheduleRepository repository;

    @Before
    public void setUp() throws Exception {
        when(collectionReferenceMock.add(any())).thenReturn(Tasks.forResult(docRefMock));
        repository = new ScheduleRepository(dbMock, collectionReferenceMock);
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
        when(queryMock.isEmpty()).thenReturn(true);
        repository.resolveTaskWithListResult(Tasks.forResult(queryMock), dataListLiveData, Schedule.class);
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

        when(queryMock.isEmpty()).thenReturn(false);
        when(queryMock.iterator()).thenReturn(queryMockList.iterator());

        repository.resolveTaskWithListResult(Tasks.forResult(queryMock), dataListLiveData, Schedule.class);
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
        Schedule data = Schedule.getPrototypeSchedule();
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
        assertFalse(resultDataSuccess.isComplete());
    }

    @Test
    public void addDocumentWithManualId() {
    }

    @Test
    public void removeDocument() {
    }

    @Test
    public void updateDocument() {
    }

    @Test
    public void getDocumentByUid() {
    }

    @Test
    public void getDocumentByAttribute() {
    }

    @Test
    public void getListOfDocumentByAttribute() {
    }

    @Test
    public void getListOfDocumentByArrayContains() {
    }

    @Test
    public void getDocumentsByListOfUid() {
    }

    @Test
    public void getDocumentNotFoundException() {
    }
}