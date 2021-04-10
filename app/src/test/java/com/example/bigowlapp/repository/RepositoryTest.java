package com.example.bigowlapp.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTest {

    @Mock
    private FirebaseFirestore dbMock;
    @Mock
    private CollectionReference collectionReferenceMock;
    @Mock
    private DocumentReference docRefMock;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ScheduleRepository repository;

    @Before
    public void setUp() throws Exception {
        when(collectionReferenceMock.add(any())).thenReturn(Tasks.forResult(docRefMock));

        repository = new ScheduleRepository(dbMock, collectionReferenceMock);
    }

    @Test
    public void listenToCollection() {
    }

    @Test
    public void addDocument() {
        Schedule data = Schedule.getPrototypeSchedule();
        assertNull(data.getUid());

        when(docRefMock.getId()).thenReturn("addedIdToData");

        // success result
        LiveDataWithStatus<Schedule> resultDataSuccess = repository.addDocument(data);
        resultDataSuccess.observeForever(schedule -> {
            assertEquals(LiveDataWithStatus.Status.SUCCESS, resultDataSuccess.getStatus());
            assertNotNull(resultDataSuccess.getValue().getUid());
            assertEquals("addedIdToData", resultDataSuccess.getValue().getUid());
        });

        // failure result
        when(collectionReferenceMock.add(any())).thenReturn(Tasks.forException(new Exception()));
        LiveDataWithStatus<Schedule> resultDataFail = repository.addDocument(data);
        resultDataFail.observeForever(schedule -> {
            assertEquals(LiveDataWithStatus.Status.ERROR, resultDataFail.getStatus());
            assertNull(resultDataFail.getValue());
        });
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
    public void extractListOfDataToModel() {
    }

    @Test
    public void resolveTaskWithListResult() {
    }

    @Test
    public void getDocumentNotFoundException() {
    }
}