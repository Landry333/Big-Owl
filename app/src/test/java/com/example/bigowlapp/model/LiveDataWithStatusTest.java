package com.example.bigowlapp.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LiveDataWithStatusTest<T> {

    private LiveDataWithStatus<T> liveDataWithStatus;

    @Before
    public void setUp() throws Exception {
        liveDataWithStatus = new LiveDataWithStatus<>();
    }

    @Test
    public void initStatus() {
        assertEquals(LiveDataWithStatus.Status.NONE, liveDataWithStatus.getStatus());
        assertNull(liveDataWithStatus.getError());
    }

    @Test
    public void setSuccess() {
    }

    @Test
    public void setError() {
    }

    @Test
    public void postSuccess() {
    }

    @Test
    public void postError() {
    }

    @Test
    public void isComplete() {
    }

    @Test
    public void hasError() {
    }

    @Test
    public void getStatus() {
    }

    @Test
    public void getError() {
    }
}