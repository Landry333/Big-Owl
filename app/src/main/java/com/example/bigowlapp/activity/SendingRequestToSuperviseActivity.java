package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.NotificationRepository;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.google.firebase.Timestamp;

import java.util.List;

public class SendingRequestToSuperviseActivity extends BigOwlActivity {
    String otherUserID;
    String currentUserID;
    String noteText;
    User otherUser;
    boolean aRequestAlready;
    boolean shouldCancelRequest = false;
    boolean shouldSendAnOtherRequest = false;
    String supBtnSend = "Send request";
    String supBtnCancel = "Cancel request";
    String canNotSend = "Can not send ";
    String sendNewRequest = "Send new request";
    private Button supRequestBtn;
    String requestUID;
    private TextView noteTv;
    private TextView resultNoteTv;
    private TextView secondResultNoteTv;
    String canCancel = "You request is pending. You can cancel it";
    String noRequest = "You presently have NO request to supervise this user ";
    String noSelfRequest = "This contact matches you as a contact. You can't send a request to yourself";
    String superviseAlready = "You already have an accepted request to supervise this user";
    String requestRejected = "Your last request was rejected by this user. You can send a new request";

    private NotificationRepository otherUserNotificationRepository;
    private RepositoryFacade repositoryFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supRequestBtn = findViewById(R.id.sup_request);
        noteTv = findViewById(R.id.note);
        resultNoteTv = findViewById(R.id.note2);
        resultNoteTv.setVisibility(View.VISIBLE);
        secondResultNoteTv = findViewById(R.id.note3);
        secondResultNoteTv.setVisibility(View.GONE);
        repositoryFacade = RepositoryFacade.getInstance();

        otherUser = getIntent().getParcelableExtra("user");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(repositoryFacade.getAuthRepository().getCurrentUser() == null) {
            return;
        }

        otherUserID = otherUser.getUid();
        currentUserID = repositoryFacade.getCurrentUserUid();

        otherUserNotificationRepository = repositoryFacade.getNotificationRepository(otherUserID);

        String contactDetails = getIntent().getStringExtra("contactDetails");
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
            otherUserNotificationRepository.addDocument(supervisionRequest);
        } else if (shouldCancelRequest) {
            otherUserNotificationRepository.removeDocument(requestUID);
        } else if (shouldSendAnOtherRequest) {
            otherUserNotificationRepository.removeDocument(requestUID);
            otherUserNotificationRepository.addDocument(supervisionRequest);
        }

        observeRequests();
    }

    private void observeRequests() {
        aRequestAlready = false; // A false value allows for search for an existing request with
        // in repository until one is found
        supRequestBtn.setText(supBtnSend); // Default setText
        supRequestBtn.setClickable(true);
        resultNoteTv.setText(noRequest);
        secondResultNoteTv.setVisibility(View.GONE);
        resultNoteTv.setVisibility(View.VISIBLE);

        LiveData<List<SupervisionRequest>> senderRequestsData = otherUserNotificationRepository
                .getListOfDocumentByAttribute(SupervisionRequest.Field.SENDER_UID, currentUserID,
                        SupervisionRequest.class);

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
}

