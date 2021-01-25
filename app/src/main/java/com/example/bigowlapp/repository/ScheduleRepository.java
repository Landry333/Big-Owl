package com.example.bigowlapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class ScheduleRepository extends Repository<Schedule> {

    // TODO: Add dependency injection
    public ScheduleRepository() {
        super("schedules");
    }

    public Task<Void> updateScheduleMemberResponse(String scheduleId, String userUId, UserScheduleResponse currentUserScheduleResponse) {
        return collectionReference.document(scheduleId).update("members.".concat(userUId), currentUserScheduleResponse);
    }

    public MutableLiveData<List<Schedule>> getListSchedulesFromGroupForUser(String groupID, String userID) {
        MutableLiveData<List<Schedule>> listOfTData = new MutableLiveData<>();
        collectionReference.whereEqualTo("groupUId", groupID)
                .whereArrayContains("memberList", userID)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tDocs = task.getResult();
                        if (tDocs != null && !tDocs.isEmpty()) {
                            listOfTData.setValue(this.extractListOfDataToModel(task.getResult(), Schedule.class));
                        } else {
                            listOfTData.setValue(null);
                        }
                    } else {
                        Log.e(getClassName(), "Error getting documents: " +
                                task.getException());
                    }
                });
        return listOfTData;
    }


}