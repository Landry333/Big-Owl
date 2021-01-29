package com.example.bigowlapp.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryFacadeTest {

    private RepositoryFacade firstRepositoryFacade;
    private RepositoryFacade secondRepositoryFacade;

    @Before
    public void setUp() throws Exception {
        firstRepositoryFacade = RepositoryFacade.getInstance();
        secondRepositoryFacade = RepositoryFacade.getInstance();
    }

    @Test
    public void getInstance() {
        // Check that getInstance always gives the same object as this is a singleton
        assertEquals(firstRepositoryFacade, secondRepositoryFacade);
    }
}