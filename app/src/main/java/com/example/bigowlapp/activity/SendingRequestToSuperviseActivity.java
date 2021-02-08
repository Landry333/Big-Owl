package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.SupervisionRequest;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.repository.AuthRepository;
import com.example.bigowlapp.repository.NotificationRepository;
import com.google.firebase.Timestamp;

import java.util.List;

// TODO: Should be using a viewmodel
public class SendingRequestToSuperviseActivity extends AppCompatActivity {
    String otherUserID;
    String currentUserID;
    String noteText;
    User otherUser;
    boolean aRequestAlready;
    boolean shouldCancelRequest = false;
    boolean shouldSendAnOtherRequest = false;
    String supBtnSend = "Send a request to supervise this user";
    String supBtnAlready = "You are supervising this user";
    String supBtnCancel = "You have sent a request already. Cancel request";
    String canNotSend = "Can not send ";
    String sendNewRequest = "Send a new request";
    private Button supRequestBtn;
    private final AuthRepository authRepository = new AuthRepository();
    String requestUID;
    private TextView noteTv;
    private TextView resultNoteTv;
    String canCancel = "You currently have a pending request to supervise this user";
    String noRequest = "You presently have NO request to supervise this user ";
    String noSelfRequest = "This contact matches you as a contact. You can't send a request to yourself";
    String superviseAlready = "You already have an accepted request to supervise this user";
    String requestRejected = "Your last request was rejected by this user. You can send a new request";

    NotificationRepository notificationRepository = new NotificationRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sending_request_to_supervise);
        otherUser = getIntent().getParcelableExtra("user");
        assert otherUser != null;
        otherUserID = otherUser.getUid();
        supRequestBtn = findViewById(R.id.SupRequest);
        currentUserID = authRepository.getCurrentUser().getUid();
        noteTv = findViewById(R.id.note);
        resultNoteTv = findViewById(R.id.note2);
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
                supRequestBtn.setOnClickListener(v -> doRequest());
            } catch (Exception e) {
                Log.e("BigOwl", Log.getStackTraceString(e));
            }
        }
    }

    private void doRequest() {
        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.setReceiverUId(otherUserID);
        supervisionRequest.setSenderUId(currentUserID);
        supervisionRequest.setResponse(SupervisionRequest.Response.NEUTRAL);
        supervisionRequest.setGroupUId(""); // TODO think about creating and setting group IDs
        supervisionRequest.setTime(Timestamp.now());

        if (!aRequestAlready) {
            notificationRepository.addDocument(supervisionRequest);
        } else if (shouldCancelRequest) {
            notificationRepository.removeDocument(requestUID);
        } else if (shouldSendAnOtherRequest) {
            notificationRepository.removeDocument(requestUID);
            notificationRepository.addDocument(supervisionRequest);
        }


        observeRequests();
    }

    private void observeRequests() {
        aRequestAlready = false; // A false value allows for search for an existing request with
        // in repository until one is found
        supRequestBtn.setText(supBtnSend); // Default setText
        resultNoteTv.setText(noRequest);
        LiveData<List<SupervisionRequest>> senderRequestsData = notificationRepository.getListOfSupervisionRequestByAttribute("senderUId", currentUserID, SupervisionRequest.class);
        senderRequestsData.observe(this, senderRequests -> {
            if (senderRequests == null)
                return;
            for (SupervisionRequest senderRequest : senderRequests) {
                if (aRequestAlready) {
                    break;
                } else if (senderRequest.getReceiverUId().equals(otherUser.getUid())) {

                    if (senderRequest.getResponse().equals(SupervisionRequest.Response.ACCEPT)) {
                        supRequestBtn.setText(supBtnAlready);
                        supRequestBtn.setClickable(false);
                        aRequestAlready = true;
                        resultNoteTv.setText(superviseAlready);
                    } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.NEUTRAL)) {
                        supRequestBtn.setText(supBtnCancel);
                        supRequestBtn.setClickable(true);
                        resultNoteTv.setText(canCancel);
                        aRequestAlready = true;
                        shouldCancelRequest = true;
                        requestUID = senderRequest.getUid();
                    } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.REJECT)) {
                        supRequestBtn.setText(sendNewRequest);
                        supRequestBtn.setClickable(true);
                        resultNoteTv.setText(requestRejected);
                        aRequestAlready = true;
                        shouldSendAnOtherRequest = true;
                        requestUID = senderRequest.getUid();
                    }

                }
            }

        });
    }
}
