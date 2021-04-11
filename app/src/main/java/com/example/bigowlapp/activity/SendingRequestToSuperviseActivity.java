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
    private User otherUser;
    private boolean aRequestAlready;
    private boolean shouldCancelRequest = false;
    private boolean shouldSendAnOtherRequest = false;
    private Button supRequestBtn;
    private String requestUID;
    private TextView noteTv;
    private TextView resultNoteTv;
    private TextView secondResultNoteTv;

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

        String noteText = "Contact: " + contactDetails + " is already registered to the application.";
        noteTv.setText(noteText);

        if (currentUserID.equals(otherUserID)) {
            resultNoteTv.setText(getString(R.string.no_self_request));
            supRequestBtn.setClickable(false);
            supRequestBtn.setText(getString(R.string.cant_send));
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
        supRequestBtn.setText(getString(R.string.send_request));
        supRequestBtn.setClickable(true);
        resultNoteTv.setText(getString(R.string.no_request));
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
                        resultNoteTv.setText(getString(R.string.supervising_already));
                        secondResultNoteTv.setVisibility(View.GONE);
                        resultNoteTv.setVisibility(View.VISIBLE);
                        supRequestBtn.setText(getString(R.string.cant_send));
                        supRequestBtn.setClickable(false);
                        aRequestAlready = true;
                    } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.NEUTRAL)) {
                        supRequestBtn.setText(getString(R.string.cancel_request));
                        supRequestBtn.setClickable(true);
                        secondResultNoteTv.setText(getString(R.string.can_cancel));
                        secondResultNoteTv.setVisibility(View.VISIBLE);
                        resultNoteTv.setVisibility(View.GONE);
                        aRequestAlready = true;
                        shouldCancelRequest = true;
                        requestUID = senderRequest.getUid();
                    } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.REJECT)) {
                        supRequestBtn.setText(getString(R.string.send_new_request));
                        supRequestBtn.setClickable(true);
                        resultNoteTv.setText(getString(R.string.request_rejected));
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

