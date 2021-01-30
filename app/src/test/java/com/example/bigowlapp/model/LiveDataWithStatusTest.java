package com.example.bigowlapp.model;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class LiveDataWithStatusTest {

    private LiveDataWithStatus<String> liveDataWithStatus;
    private String testName;
    private Exception testException;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        liveDataWithStatus = new LiveDataWithStatus<>();
        testName = "John Smith";
        testException = new Exception("John Smith Exception");
    }

    @Test
    public void initStatusWithoutData() {
        assertEquals(LiveDataWithStatus.Status.NONE, liveDataWithStatus.getStatus());
        assertNull(liveDataWithStatus.getError());
    }

    @Test
    public void initStatusWithData() {
        liveDataWithStatus = new LiveDataWithStatus<>(null);
        assertEquals(LiveDataWithStatus.Status.NONE, liveDataWithStatus.getStatus());
        assertNull(liveDataWithStatus.getError());
        assertNull(liveDataWithStatus.getValue());
    }

    @Test
    public void setSuccess() {
        liveDataWithStatus.setSuccess(testName);
        assertEquals(LiveDataWithStatus.Status.SUCCESS, liveDataWithStatus.getStatus());
        assertEquals(testName, liveDataWithStatus.getValue());
    }

    @Test
    public void setError() {
        liveDataWithStatus.setError(testException);
        assertEquals(LiveDataWithStatus.Status.ERROR, liveDataWithStatus.getStatus());
        assertEquals(testException, liveDataWithStatus.getError());
    }

    @Test
    public void postSuccess() {
        liveDataWithStatus.postSuccess(testName);
        assertEquals(LiveDataWithStatus.Status.SUCCESS, liveDataWithStatus.getStatus());
        assertEquals(testName, liveDataWithStatus.getValue());
    }

    @Test
    public void postError() {
        liveDataWithStatus.postError(testException);
        assertEquals(LiveDataWithStatus.Status.ERROR, liveDataWithStatus.getStatus());
        assertEquals(testException, liveDataWithStatus.getError());
    }

    @Test
    public void isComplete() {
        assertFalse(liveDataWithStatus.isComplete());
        liveDataWithStatus.setError(null);
        assertTrue(liveDataWithStatus.isComplete());
        liveDataWithStatus.setSuccess(null);
        assertTrue(liveDataWithStatus.isComplete());
    }

    @Test
    public void hasError() {
        assertFalse(liveDataWithStatus.hasError());
        liveDataWithStatus.setSuccess(null);
        assertFalse(liveDataWithStatus.hasError());
        liveDataWithStatus.setError(null);
        assertTrue(liveDataWithStatus.hasError());
    }
}