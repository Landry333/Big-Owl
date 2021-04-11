package com.example.bigowlapp.view_model;

import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.ScheduleRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleListViewModelTest {

    private String groupUid;
    private String userUid;

    @Mock
    private RepositoryFacade repositoryFacadeMock;

    @Mock
    private ScheduleRepository scheduleRepositoryMock;

    private ScheduleListViewModel scheduleListViewModel;

    @Before
    public void setUp() {
        groupUid = "GroupUid";
        userUid = "UserUid";
        scheduleListViewModel = new ScheduleListViewModel();
        scheduleListViewModel.setRepositoryFacade(repositoryFacadeMock);
        when(repositoryFacadeMock.getScheduleRepository())
                .thenReturn(scheduleRepositoryMock);
        when(repositoryFacadeMock.getCurrentUserUid()).thenReturn(userUid);
    }

    @Test
    public void getScheduleListSupervisor() {
        scheduleListViewModel.getScheduleList(true, groupUid);
        verify(scheduleRepositoryMock).getAllSchedulesForSupervisor(userUid);
    }

    @Test
    public void getScheduleListWhenNotSupervisor() {
        scheduleListViewModel.getScheduleList(false, groupUid);
        verify(scheduleRepositoryMock).getListSchedulesFromGroupForUser(groupUid, userUid);
    }
}