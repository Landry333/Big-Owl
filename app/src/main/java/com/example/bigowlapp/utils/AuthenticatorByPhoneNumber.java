package com.example.bigowlapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;

public class AuthenticatorByPhoneNumber {

    private String deviceIdNumber;
    private String devicePhoneNumber;
    private String currentUserPhoneNumber;
    private final Context context;
    private final AuthRepository authRepository = new AuthRepository();
    private RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    public AuthenticatorByPhoneNumber(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission") // Permission has already been granted after sign in step
    public void authenticate(String scheduleId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        devicePhoneNumber = telephonyManager.getLine1Number();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceIdNumber = telephonyManager.getImei();
            if (deviceIdNumber == null) {
                deviceIdNumber = telephonyManager.getMeid();
            }
        } else {
            deviceIdNumber = telephonyManager.getDeviceId();
        }
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(schedule -> {
                    repositoryFacade.getUserRepository()
                            .getDocumentByUid(repositoryFacade.getAuthRepository().getCurrentUser().getUid(), User.class)
                            .observeForever(user -> {
                                currentUserPhoneNumber = user.getPhoneNumber();
                                UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                                        .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                                Attendance attendance = userScheduleResponse.getAttendance();
                                if (currentUserPhoneNumber.equalsIgnoreCase("+" + devicePhoneNumber)) {
                                    attendance.setAuthenticated(true);
                                    Toast.makeText(context, "SUCCESS in authentication for your next BIG OWL schedule", Toast.LENGTH_LONG).show();
                                } else {
                                    attendance.setAuthenticated(false);
                                    userScheduleResponse.getAttendance().setDeviceIdNumber(deviceIdNumber);
                                    AuthByPhoneNumberFailure authByPhoneNumberFailure = new AuthByPhoneNumberFailure();
                                    authByPhoneNumberFailure.setScheduleId(scheduleId);
                                    authByPhoneNumberFailure.setCreationTime(Timestamp.now());
                                    authByPhoneNumberFailure.setSenderPhoneNum(currentUserPhoneNumber);
                                    authByPhoneNumberFailure.setReceiverUid(schedule.getGroupSupervisorUid());
                                    authByPhoneNumberFailure.setSenderUid(authRepository.getCurrentUser().getUid());
                                    repositoryFacade.getNotificationRepository().addDocument(authByPhoneNumberFailure);
                                }
                                attendance.setAttemptedAuthByUserMobileNumber(true);
                                repositoryFacade.getScheduleRepository().updateDocument(scheduleId, schedule);
                            });
                });

    }

}
