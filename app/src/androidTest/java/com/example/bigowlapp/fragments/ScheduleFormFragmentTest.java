package com.example.bigowlapp.fragments;

import android.os.SystemClock;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.viewModel.SetScheduleViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
public class ScheduleFormFragmentTest {

    private FragmentScenario<ScheduleFormFragment> fragmentScenario;
    ScheduleFormFragment scheduleFormFragment;

    @Mock
    private SetScheduleViewModel mockSetScheduleViewModel;

    // Fake data
    private Schedule fakeSchedule;
    private MutableLiveData<Schedule> newScheduleData;
    private Group fakeSelectedGroup;
    private MutableLiveData<Group> groupData;
    private CarmenFeature fakeSelectedLocation;
    private MutableLiveData<CarmenFeature> locationData;
    private List<Group> fakeListOfGroup;
    private MutableLiveData<List<Group>> listOfGroupData;
    private List<User> fakeListOfUsers;
    private MutableLiveData<List<User>> listOfUsersData;
    private List<User> fakeSelectedListOfUsers;
    private MutableLiveData<List<User>> selectedListOfUsersData;

    // Views
    private ViewInteraction editTitle;
    private ViewInteraction groupButton;
    private ViewInteraction selectUserButton;
    private ViewInteraction usersListView;
    private ViewInteraction editStartDate;
    private ViewInteraction editStartTime;
    private ViewInteraction editEndDate;
    private ViewInteraction editEndTime;
    private ViewInteraction editLocation;
    private ViewInteraction confirmSetSchedule;

    private ViewInteraction confirmUserselection;
    private ViewInteraction cancelUserselection;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        setupViews();

        // assume User data is set
        when(mockSetScheduleViewModel.isCurrentUserSet()).thenReturn(true);

        // setup fake data for schedules initial state
        fakeSchedule = Schedule.getPrototypeSchedule();
        newScheduleData = new MutableLiveData<>(fakeSchedule);
        when(mockSetScheduleViewModel.getNewScheduleData()).thenReturn(newScheduleData);

        // setup fake data for selectedGroup
        fakeSelectedGroup = null;
        groupData = new MutableLiveData<>(fakeSelectedGroup);
        when(mockSetScheduleViewModel.getSelectedGroupData()).thenReturn(groupData);

        // setup fake data for selectedLocation
        fakeSelectedLocation = null;
        locationData = new MutableLiveData<>(fakeSelectedLocation);
        when(mockSetScheduleViewModel.getSelectedLocationData()).thenReturn(locationData);

        // setup user Group list
        fakeListOfGroup = Collections.singletonList(new Group("1", "group_name"));
        listOfGroupData = new MutableLiveData<>(fakeListOfGroup);
        when(mockSetScheduleViewModel.getListOfGroup()).thenReturn(listOfGroupData);

        // setup users in group data
        fakeListOfUsers = Collections.singletonList(new User("2", "Jon", "James", "jon@email.com"));
        listOfUsersData = new MutableLiveData<>(fakeListOfUsers);
        when(mockSetScheduleViewModel.getListOfUserInGroupData()).thenReturn(listOfUsersData);

        // setup users in group data
        fakeSelectedListOfUsers = new ArrayList<>();
        selectedListOfUsersData = new MutableLiveData<>(fakeSelectedListOfUsers);
        when(mockSetScheduleViewModel.getListOfSelectedUsersData()).thenReturn(selectedListOfUsersData);

        // initialize the fragment with a mocked viewModel
        fragmentScenario = FragmentScenario.launchInContainer(ScheduleFormFragment.class, null, R.style.AppTheme);
        fragmentScenario.moveToState(Lifecycle.State.CREATED);
        fragmentScenario.onFragment(scheduleFormFragment -> {
            scheduleFormFragment.setSetScheduleViewModel(mockSetScheduleViewModel);
            this.scheduleFormFragment = scheduleFormFragment;
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void exampleTest() {
        // TODO: Remove this
        SystemClock.sleep(30000);
    }

    @Test
    public void shouldDisplayErrorsIfNoFilledEntries() {
        // click on setSchedule without any form data filled
        confirmSetSchedule.perform(click());
        // check the error indicators correctly show up
        editTitle.check(matches(hasErrorText("Please enter title.")));
        assertEquals("Please select a group", scheduleFormFragment.getGroupButton().getError().toString());
        assertEquals("Please select at least one user", scheduleFormFragment.getSelectUserButton().getError().toString());
        assertEquals("Please select a location", scheduleFormFragment.getEditLocation().getError().toString());
        // TODO: check for toast
    }

    // TODO: add more verifies
    @Test
    public void completeAScheduleForm() {
        // Set up Title
        editTitle.perform(replaceText("Schedule_Title"));
        fakeSchedule.setTitle("Schedule_Title");

        // Select a Group
        groupButton.perform(click());
        fakeSelectedGroup = fakeListOfGroup.get(0);
        when(mockSetScheduleViewModel.getSelectedGroup()).thenReturn(fakeListOfGroup.get(0));
        onView(withText("group_name")).perform(click());
        groupData.postValue(fakeListOfGroup.get(0));
        fakeSchedule.setGroupUId(fakeSelectedGroup.getUid());
        // TODO: maybe verify update before hand
        listOfUsersData.postValue(fakeListOfUsers);

        // Select Users from Group
        SystemClock.sleep(2000);
        selectUserButton.perform(click());
        onView(withText("1")).perform(click());
        confirmUserselection.perform(click());
        // TODO: maybe verify update before hand
        selectedListOfUsersData.postValue(fakeListOfUsers);
        fakeSchedule.setMemberList(Arrays.asList(fakeListOfUsers.stream().map(u -> u.getUid()).collect(Collectors.joining())));

        // Modify End Date
        // --> setup date to add
        Calendar newEndTime = Calendar.getInstance();
        newEndTime.set(2023, 6, 15);
        // --> do modification
        editStartDate.perform(click());
        onView(withClassName((Matchers.equalTo(DatePicker.class.getName())))).perform(PickerActions.setDate(2023, 6, 15));
        onView(withText("OK")).perform(click());
        // TODO: maybe verify update before hand
        fakeSchedule.setEndTime(new Timestamp(newEndTime.getTime()));
        newScheduleData.postValue(fakeSchedule);

        // Modify End Time
        // --> setup time to add
        newEndTime.set(Calendar.HOUR_OF_DAY, 16);
        newEndTime.set(Calendar.MINUTE, 23);
        // --> do modification
        editEndTime.perform(click());
        onView(withClassName((Matchers.equalTo(TimePicker.class.getName())))).perform(PickerActions.setTime(16, 23));
        onView(withText("OK")).perform(click());
        // TODO: maybe verify update before hand
        fakeSchedule.setEndTime(new Timestamp(newEndTime.getTime()));
        newScheduleData.postValue(fakeSchedule);

        // Select A location
        // --> setup location data
        CarmenFeature location = CarmenFeature.builder()
                .placeName("Concordia University")
                .rawCenter(new double[]{-88.00, 88.00})
                .build();
        GeoPoint locationCoordinate = new GeoPoint(location.center().latitude(),
                location.center().longitude());
        // --> do ui actions
        editLocation.perform(click());
        pressBack();
        // TODO: maybe verify update before hand
        fakeSelectedLocation = location;
        locationData.postValue(fakeSelectedLocation);
        fakeSchedule.setLocation(locationCoordinate);

        // Click on Set Schedule
        confirmSetSchedule.perform(click());
        // TODO: check for toast or check the fragment was destroyed
    }

    private void setupViews() {
        // ScheduleFormFragment Views
        editTitle = onView(withId(R.id.edit_title_schedule));
        groupButton = onView(withId(R.id.select_group_button));
        selectUserButton = onView(withId(R.id.select_user_button));
        usersListView = onView(withId(R.id.select_users_list_view));
        editStartDate = onView(withId(R.id.edit_start_date));
        editStartTime = onView(withId(R.id.edit_start_time));
        editEndDate = onView(withId(R.id.edit_end_date));
        editEndTime = onView(withId(R.id.edit_end_time));
        confirmSetSchedule = onView(withId(R.id.set_schedule_confirm_button));
        editLocation = onView(withId(R.id.edit_location));
        // User Fragment Views
        confirmUserselection = onView(withId(R.id.user_item_list_confirm));
        cancelUserselection = onView(withId(R.id.user_item_list_cancel));
    }
}