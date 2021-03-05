package com.example.bigowlapp.utils;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class IsSmsSenderACurrentEventSupervisor extends AppCompatActivity {

    //private String toDisplay;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /*public String getToDisplay() {
        return toDisplay;
    }

    public void setToDisplay(String toDisplay) {
        this.toDisplay = toDisplay;
    }*/

    public IsSmsSenderACurrentEventSupervisor() {

    }

    public Task<List<Schedule>> check(String phoneNumber) {
        RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();
        String currentUserUid = repositoryFacade.getAuthRepository().getCurrentUser().getUid();
        //Timestamp currentTime = new Timestamp(Calendar.getInstance().getTime());

        Task<QuerySnapshot> gettingUserTask = db.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get();
        Task<User> userTask = gettingUserTask.continueWithTask(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    List<User> listOfUsers = task.getResult().toObjects(User.class);
                    Log.e("listOfusers", listOfUsers.toString());
                    //Log.e("forResult_listOfUsers", Tasks.forResult(listOfUsers).toString());

                    return Tasks.forResult(listOfUsers.get(0));

                } else throw task.getException();

            } else throw task.getException();

        });


        Task<List<Schedule>> schedulesTask = userTask.continueWithTask(task -> {
            if (task.isSuccessful()) {
                User supervisorUser = task.getResult();

                Task<QuerySnapshot> gettingSchedulesTask = db.collection("schedules")
                        .whereEqualTo("groupSupervisorUid", supervisorUser.getUid())
                        .whereArrayContains("memberList", currentUserUid)
                        .get();

                Task<List<Schedule>> handleSchedulesTask = gettingSchedulesTask.continueWithTask(task2 ->{
                    if(task2.isSuccessful()){
                        if(!task2.getResult().isEmpty()){
                            List<Schedule> listOfSchedules = task2.getResult().toObjects(Schedule.class);
                            return Tasks.forResult(listOfSchedules);
                        }
                        else{
                            throw task2.getException();
                        }
                    }
                    else {
                        throw task2.getException();
                    }
                });

                return handleSchedulesTask;

            }
            else {
                throw task.getException();
            }

        });
        return schedulesTask;
    }
}


/*LiveDataWithStatus<User> supervisorData = userRepository.getDocumentByAttribute("phoneNumber", phoneNumber, User.class);
        supervisorData.observe( this, supervisor -> {
            //if (supervisorData.hasError())
            setToDisplay(supervisor.toString());
            if(supervisor == null)
                return;

            else {
                found.set(true);
            }

            // handle success
            LiveDataWithStatus<List<Schedule>> possibleSchedulesData = repositoryFacade.getScheduleRepository().getSchedulesBySupervisor(supervisor.getUid(), currentUserUid);
            possibleSchedulesData.observe( this, listOfSchedules -> {
                if (possibleSchedulesData.hasError()) {
                    // handle or ignore error here
                    return;
                }

                // do whatever you want with the list of schedules


                Timestamp currentTime = new Timestamp(Calendar.getInstance().getTime());

                for (Schedule schedule : listOfSchedules) {
                    if(Math.abs(schedule.getStartTime().getSeconds()-currentTime.getSeconds())<1800){
                        found.set(true);
                        return;
                    }
                }

            });


        });*/
