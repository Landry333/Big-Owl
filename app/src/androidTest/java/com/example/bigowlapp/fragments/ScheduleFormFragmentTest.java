package com.example.bigowlapp.fragments;

import android.os.SystemClock;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
public class ScheduleFormFragmentTest {

    private FragmentScenario<ScheduleFormFragment> fragmentScenario;

    @Mock
    private SetScheduleViewModel mockSetScheduleViewModel;

    private Schedule fakeSchedule;
    private MutableLiveData<Schedule> newScheduleData;
    private Group fakeSelectedGroup;
    private LiveData<Group> groupData;
    private CarmenFeature fakeSelectedLocation;
    private LiveData<CarmenFeature> locationData;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        // setup fake data for schedules initial state
        fakeSchedule = Schedule.getPrototypeSchedule();
        newScheduleData = new MutableLiveData<>(fakeSchedule);
        when(mockSetScheduleViewModel.getNewScheduleData()).thenReturn(newScheduleData);

        // setup fake data for selectedGroup
        fakeSelectedGroup = null;
        groupData = new MutableLiveData<>(fakeSelectedGroup);
        when(mockSetScheduleViewModel.getSelectedGroupData()).thenReturn(groupData);

        // setup fake data for selectedGroup
        fakeSelectedLocation = null;
        locationData = new MutableLiveData<>(fakeSelectedLocation);
        when(mockSetScheduleViewModel.getSelectedLocationData()).thenReturn(locationData);


        fragmentScenario = FragmentScenario.launchInContainer(ScheduleFormFragment.class, null,  R.style.AppTheme);
        fragmentScenario.moveToState(Lifecycle.State.CREATED);
        fragmentScenario.onFragment(scheduleFormFragment -> {
            scheduleFormFragment.setSetScheduleViewModel(mockSetScheduleViewModel);
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void exampleTest() {
        // TODO: Remove this
        SystemClock.sleep(30000);
    }
}