package com.example.bigowlapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.example.bigowlapp.R;
import com.example.bigowlapp.activity.BigOwlActivity;
import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AuthenticatorByDeviceId {

    private String deviceIdNumber;
    private TextView deviceIdDisplay;
    private TextView authenticationStatusDisplay;

    RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    private final Context context;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AuthenticatorByDeviceId(Context context) {
        this.context = context;
    }

    /*public String getDeviceIdNumber() {
        return deviceIdNumber;
    }*/

    @SuppressLint("MissingPermission")
    protected void authenticate(String scheduleId) {
        //String scheduleId = getIntent().getStringExtra("scheduleId");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceIdNumber = telephonyManager.getDeviceId();
        //deviceIdDisplay = findViewById(R.id.device_Id_Display_2);
        //deviceIdDisplay.setText("Device ID Number: " + deviceIdNumber);

        //authenticationStatusDisplay = findViewById(R.id.authentication_Status_Display_2);
        /*repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observe((LifecycleOwner) context, schedule -> {
                    UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                            .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                    Attendance attendance = userScheduleResponse.getAttendance();
                    String savedDeviceId = attendance.getDeviceIdNumber();
                    Attendance.LocatedStatus scheduleLocated = attendance.getScheduleLocated();

                    if (scheduleLocated == Attendance.LocatedStatus.CORRECT_LOCATION && deviceIdNumber.equalsIgnoreCase(savedDeviceId)) {
                        authenticationStatusDisplay.setText("Authentication Status: SUCCEEDED");
                        attendance.setAuthenticated(true);
                    } else {
                        authenticationStatusDisplay.setText("Authentication Status: FAILED");
                        attendance.setAuthenticated(false);
                    }
                    attendance.setAuthAttemptedPhoneUid(true);
                    repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                });*/

    }

    public Task<Schedule> check(String scheduleId) {
        RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();
        LiveDataWithStatus<Schedule> schedule = repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId,Schedule.class);
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

}
