package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.view_model.SendingRequestToSuperviseViewModel;
import com.google.firebase.Timestamp;

import java.util.List;

public class SendingRequestToSuperviseActivity extends BigOwlActivity {
    private String otherUserID;
    private String currentUserID;
    private String noteText;
    private User otherUser;
    private boolean aRequestAlready;
    private boolean shouldCancelRequest = false;
    private boolean shouldSendAnOtherRequest = false;
    private Button supRequestBtn;
    private String requestUID;
    private TextView noteTv;
    private TextView resultNoteTv;
    private TextView secondResultNoteTv;

    private static final String supBtnSend = "Send request";
    private static final String supBtnCancel = "Cancel request";
    private static final String canNotSend = "Can not send ";
    private static final String sendNewRequest = "Send new request";
    private static final String canCancel = "You request is pending. You can cancel it";
    private static final String noRequest = "You presently have NO request to supervise this user ";
    private static final String noSelfRequest = "This contact matches you as a contact. You can't send a request to yourself";
    private static final String superviseAlready = "You already have an accepted request to supervise this user";
    private static final String requestRejected = "Your last request was rejected by this user. You can send a new request";

    private SendingRequestToSuperviseViewModel sRTSViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supRequestBtn = findViewById(R.id.sup_request);
        noteTv = findViewById(R.id.note);
        resultNoteTv = findViewById(R.id.note2);
        resultNoteTv.setVisibility(View.VISIBLE);
        secondResultNoteTv = findViewById(R.id.note3);
        secondResultNoteTv.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sRTSViewModel == null) {
            sRTSViewModel = new ViewModelProvider(this).get(SendingRequestToSuperviseViewModel.class);
        }
        if (!sRTSViewModel.isCurrentUserSet()) {
            return;
        }
        String contactDetails = getIntent().getStringExtra("contactDetails");
        otherUser = getIntent().getParcelableExtra("user");

        otherUserID = otherUser.getUid();
        currentUserID = sRTSViewModel.getCurrentUserUid();

        noteText = "Contact: " + contactDetails + " is already registered to the application.";
        noteTv.setText(noteText);

        if (currentUserID.equals(otherUserID)) {
            resultNoteTv.setText(noSelfRequest);
            supRequestBtn.setClickable(false);
            supRequestBtn.setText(canNotSend);
        } else {
            supRequestBtn.setClickable(true);
            try {
                observeRequests();
                supRequestBtn.setOnClickListener(v -> {
                    doRequest();
                    Toast.makeText(this, "Action submitted", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e("BigOwl", Log.getStackTraceString(e));
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sending_request_to_supervise;
    }

    private void doRequest() {
        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.setReceiverUid(otherUserID);
        supervisionRequest.setSenderUid(currentUserID);
        supervisionRequest.setResponse(SupervisionRequest.Response.NEUTRAL);
        supervisionRequest.setGroupUid("");
        supervisionRequest.setCreationTime(Timestamp.now());

        if (!aRequestAlready) {
            sRTSViewModel.sendSupervisionRequestToOtherUser(otherUserID, supervisionRequest);
        } else if (shouldCancelRequest) {
            sRTSViewModel.removeSupervisionRequestFromOtherUser(otherUserID, requestUID);
        } else if (shouldSendAnOtherRequest) {
            sRTSViewModel.removeSupervisionRequestFromOtherUser(otherUserID, requestUID);
            sRTSViewModel.sendSupervisionRequestToOtherUser(otherUserID, supervisionRequest);
        }
        observeRequests();
    }

    private void observeRequests() {
        // A false value allows for search for an existing request within repository until one is found
        aRequestAlready = false;
        // Default setText
        supRequestBtn.setText(supBtnSend);
        supRequestBtn.setClickable(true);
        resultNoteTv.setText(noRequest);
        secondResultNoteTv.setVisibility(View.GONE);
        resultNoteTv.setVisibility(View.VISIBLE);

        LiveData<List<SupervisionRequest>> senderRequestsData = sRTSViewModel
                .getRequestsDataFromOtherUser(otherUserID, currentUserID);

        senderRequestsData.observe(this, senderRequests -> {
            if (senderRequests == null)
                return;
            for (SupervisionRequest senderRequest : senderRequests) {
                if (aRequestAlready) {
                    break;
                } else if (senderRequest.getReceiverUid().equals(otherUser.getUid())) {
                    if (senderRequest.getResponse().equals(SupervisionRequest.Response.ACCEPT)) {
                        resultNoteTv.setText(superviseAlready);
                        secondResultNoteTv.setVisibility(View.GONE);
                        resultNoteTv.setVisibility(View.VISIBLE);
                        supRequestBtn.setText(canNotSend);
                        supRequestBtn.setClickable(false);
                        aRequestAlready = true;
                    } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.NEUTRAL)) {
                        supRequestBtn.setText(supBtnCancel);
                        supRequestBtn.setClickable(true);
                        secondResultNoteTv.setText(canCancel);
                        secondResultNoteTv.setVisibility(View.VISIBLE);
                        resultNoteTv.setVisibility(View.GONE);
                        aRequestAlready = true;
                        shouldCancelRequest = true;
                        requestUID = senderRequest.getUid();
                    } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.REJECT)) {
                        supRequestBtn.setText(sendNewRequest);
                        supRequestBtn.setClickable(true);
                        resultNoteTv.setText(requestRejected);
                        resultNoteTv.setVisibility(View.VISIBLE);
                        secondResultNoteTv.setVisibility(View.GONE);
                        aRequestAlready = true;
                        shouldSendAnOtherRequest = true;
                        requestUID = senderRequest.getUid();
                    }

                }
            }
        });
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setsRTSViewModel(SendingRequestToSuperviseViewModel sRTSViewModel) {
        this.sRTSViewModel = sRTSViewModel;
    }

}

