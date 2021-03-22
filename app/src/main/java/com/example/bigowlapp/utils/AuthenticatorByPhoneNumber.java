package com.example.bigowlapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.bigowlapp.model.Attendance;
import com.example.bigowlapp.model.AuthByPhoneNumberFailure;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.model.UserScheduleResponse;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.i18n.phonenumbers.NumberParseException;

public class AuthenticatorByPhoneNumber {

    private String devicePhoneNumber;
    private String currentUserPhoneNumber;
    private final Context context;
    private final AuthRepository authRepository = new AuthRepository();
    private RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();

    public AuthenticatorByPhoneNumber(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    // Permission was already granted after sign in step to be able to proceed
    public void authenticate(String scheduleId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        devicePhoneNumber = telephonyManager.getLine1Number();
        repositoryFacade.getScheduleRepository().getDocumentByUid(scheduleId, Schedule.class)
                .observeForever(schedule -> {
                    repositoryFacade.getUserRepository()
                            .getDocumentByUid(repositoryFacade.getAuthRepository().getCurrentUser().getUid(), User.class)
                            .observeForever(user -> {
                                currentUserPhoneNumber = user.getPhoneNumber();
                                UserScheduleResponse userScheduleResponse = schedule.getUserScheduleResponseMap()
                                        .get(repositoryFacade.getAuthRepository().getCurrentUser().getUid());
                                Attendance attendance = userScheduleResponse.getAttendance();
                                String formattedDevicePhoneNum;
                                try {
                                    formattedDevicePhoneNum = PhoneNumberFormatter.formatNumber(devicePhoneNumber,context);
                                } catch (NumberParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "FAILED to format phone number. Process failed", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (currentUserPhoneNumber.equalsIgnoreCase(formattedDevicePhoneNum)){
                                    attendance.setAuthenticated(true);
                                    Toast.makeText(context, "SUCCESS in authentication for your next BIG OWL schedule", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    attendance.setAuthenticated(false);
                                    userScheduleResponse.getAttendance().setAppInstanceId(FirebaseInstallations.getInstance().getId().getResult());
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
