package com.example.bigowlapp.utils;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.ScheduleRepository;
import com.example.bigowlapp.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class IsSmsSenderACurrentEventSupervisor extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<List<Schedule>> check(String phoneNumber) {
        RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

        Task<QuerySnapshot> gettingUserTask = db.collection(UserRepository.COLLECTION_NAME)
                .whereEqualTo(User.Field.PHONE_NUMBER, phoneNumber)
                .get();
        Task<User> userTask = getUserTask(gettingUserTask);

        return userTask.onSuccessTask(user ->
                getSchedulesFromUserTask(repositoryFacade.getCurrentUserUid(), user));
    }

    @NonNull
    private Task<User> getUserTask(Task<QuerySnapshot> gettingUserTask) {
        return gettingUserTask.continueWithTask(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                List<User> listOfUsers = task.getResult().toObjects(User.class);
                return Tasks.forResult(listOfUsers.get(0));
            } else {
                throw task.getException();
            }
        });
    }

    @NonNull
    private Task<List<Schedule>> getSchedulesFromUserTask(String currentUserUid, User supervisorUser) {
        Task<QuerySnapshot> gettingSchedulesTask = db.collection(ScheduleRepository.COLLECTION_NAME)
                .whereEqualTo(Schedule.Field.GROUP_SUPERVISOR_UID, supervisorUser.getUid())
                .whereArrayContains(Schedule.Field.MEMBER_LIST, currentUserUid)
                .get();
        return gettingSchedulesTask.continueWithTask(task2 -> {
            if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
                List<Schedule> listOfSchedules = task2.getResult().toObjects(Schedule.class);
                return Tasks.forResult(listOfSchedules);
            } else {
                throw task2.getException();
            }
        });
    }
}
