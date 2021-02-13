package com.example.bigowlapp.activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Group;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.GroupRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.example.bigowlapp.viewModel.SupervisedGroupListViewModel;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SupervisedGroupListActivityTest {

    @Rule
    public ActivityScenarioRule<SupervisedGroupListActivity> activityRule = new ActivityScenarioRule<>(SupervisedGroupListActivity.class);

    @Mock
    private SupervisedGroupListViewModel supervisedGroupListViewModel;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FirebaseUser testFirebaseUser;

    private List<Group> testUserSupervisedGroupList;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        User testUser = new User("abc123", "first", "last", "+911", "test@mail.com", null, null);
        MutableLiveData<User> testUserData = new MutableLiveData<>(testUser);
        testUserData.postValue(testUser);

        testUserSupervisedGroupList = new ArrayList<>();
        for (int i = 1; i < (int) ((Math.random() * 4) + 4); i++) {
            User groupSupervisor = new User(
                    "group00".concat(String.valueOf(i)).concat("supervisor"),
                    "group00".concat(String.valueOf(i)).concat("fName"),
                    "group00".concat(String.valueOf(i)).concat("lName"),
                    "+12300".concat(String.valueOf(i)),
                    "group00".concat(String.valueOf(i)).concat("@mail.com"),
                    null,
                    null
            );
            LiveDataWithStatus<User> groupSupervisorData = new LiveDataWithStatus<>(groupSupervisor);

            List<String> groupSupervisedUserId = new ArrayList<>();
            for (int j = 1; j < (int) ((Math.random() * 2) + 1); j++) {
                groupSupervisedUserId.add(
                        "group00".concat(String.valueOf(i)).concat("user00").concat(String.valueOf(j)));
            }
            groupSupervisedUserId.add("abc123");

            Group newGroup = new Group(
                    "group00".concat(String.valueOf(i)),
                    "groupName00".concat(String.valueOf(i)),
                    groupSupervisor.getUid(),
                    groupSupervisedUserId
            );
            testUserSupervisedGroupList.add(newGroup);

            when(userRepository.getDocumentByUid(groupSupervisor.getUid(), User.class))
                    .thenReturn(groupSupervisorData);
            when(supervisedGroupListViewModel.getSupervisor(groupSupervisor.getUid())).thenReturn(groupSupervisorData);
        }
        LiveDataWithStatus<List<Group>> testUserSupervisedGroupListData = new LiveDataWithStatus<>(testUserSupervisedGroupList);
        testUserSupervisedGroupListData.postValue(testUserSupervisedGroupList);

        when(testFirebaseUser.getUid()).thenReturn("abc123");
        when(authRepository.getCurrentUser()).thenReturn(testFirebaseUser);
        when(supervisedGroupListViewModel.getCurrentUserData()).thenReturn(testUserData);

        when(groupRepository.getListOfDocumentByArrayContains(
                anyString(), anyString(), eq(Group.class)))
                .thenReturn(testUserSupervisedGroupListData);
        when(supervisedGroupListViewModel.getSupervisedGroupListData()).thenReturn(testUserSupervisedGroupListData);

        activityRule.getScenario().moveToState(Lifecycle.State.CREATED);
        activityRule.getScenario().onActivity(activity -> {
            activity.setSupervisedGroupListViewModel(supervisedGroupListViewModel);
        });
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void activityListOutUserSupervisedGroupsTest() {
        verify(supervisedGroupListViewModel, times(1)).getCurrentUserData();
        verify(supervisedGroupListViewModel, times(1)).getSupervisedGroupListData();

        for (int i = 0; i < testUserSupervisedGroupList.size(); i++) {
            // check if the group names are matched and displayed
            onView(allOf(withId(R.id.text_view_group_name), withText(testUserSupervisedGroupList.get(i).getName())))
                    .check(matches(isDisplayed()));
            // check if the supervisors full names are matched and displayed
            String supervisorFullName = supervisedGroupListViewModel.getSupervisor(testUserSupervisedGroupList.get(i).getSupervisorId()).getValue().getFullName();
            onView(allOf(withId(R.id.text_view_group_supervisor), withText(supervisorFullName))).check(matches(isDisplayed()));
        }
    }
}