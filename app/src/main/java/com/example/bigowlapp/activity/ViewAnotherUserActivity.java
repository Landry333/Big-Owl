package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.view.View;
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


public class ViewAnotherUserActivity extends AppCompatActivity {
    String otherUserID;
    String currentUserID, noteText;
    User otherUser;
    boolean aRequestAlready;
    boolean shouldCancelRequest = false;
    String supBtnSend = "Send a request to supervise this user";
    String supBtnAlready = "You are supervising this user";
    String supBtnCancel = "You have sent a request already. Cancel request";
    String canNotSend = "Can not send ";
    private Button supRequestBtn;
    private AuthRepository authRepository = new AuthRepository();
    String requestUID;
    private TextView noteTv;
    private TextView noteTv2;
    String canCancel = "You currently have a pending request to supervise this user";
    String noRequest = "You presently have NO request to supervise this user ";
    String noSelfRequest = "This contact matches your contact. You can't send a request to yourself";
    String superviseAlready = "You already have an accepted request to supervise this user";

    NotificationRepository notificationRepository = new NotificationRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_another_user);
        otherUser = getIntent().getParcelableExtra("user");
        otherUserID = otherUser.getUId();
        supRequestBtn = findViewById(R.id.SupRequest);
        currentUserID = authRepository.getCurrentUser().getUid();
        noteTv = findViewById(R.id.note);
        noteTv = findViewById(R.id.note);
        noteTv2 = findViewById(R.id.note2);
        String contactDetails = getIntent().getStringExtra("contactDetails");
        noteText = "Contact: " + contactDetails + " is already registered to the application.";
        noteTv.setText(noteText);

        if (currentUserID.equals(otherUserID)) {
            noteTv2.setText(noSelfRequest);
            supRequestBtn.setClickable(false);
            supRequestBtn.setText(canNotSend);
        } else {
            supRequestBtn.setClickable(true);
            try {
                observeRequests();
                supRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DoRequest();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void DoRequest() {
        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.setReceiverUId(otherUserID);
        supervisionRequest.setSenderUId(currentUserID);
        supervisionRequest.setResponse(SupervisionRequest.Response.NEUTRAL);
        supervisionRequest.setGroupUId(""); // TODO think about creating and setting group IDs
        supervisionRequest.setTimeSent(Timestamp.now());
        supervisionRequest.setTime(Timestamp.now());

        if (!aRequestAlready) {
            notificationRepository.addDocument(supervisionRequest);

        } else if (shouldCancelRequest) {
            notificationRepository.removeDocument(requestUID);
        }

        observeRequests();
    }

    private void observeRequests() {
        aRequestAlready = false; // A false value allows for search for an existing request with
                                // in repository until one is found
        supRequestBtn.setText(supBtnSend); // Default setText
        noteTv2.setText(noRequest);
        LiveData<List<SupervisionRequest>> senderRequestsData = notificationRepository.getListOfSupervisionRequestByAttribute("senderUId", currentUserID, SupervisionRequest.class);
        senderRequestsData.observe(this, senderRequests -> {
            if (senderRequests != null) {
                for (SupervisionRequest senderRequest : senderRequests) {
                    if (aRequestAlready) {
                        break;
                    } else if (senderRequest.getReceiverUId().equals(otherUser.getUId())) {

                        if (senderRequest.getResponse().equals(SupervisionRequest.Response.ACCEPT)) {
                            supRequestBtn.setText(supBtnAlready);
                            aRequestAlready = true;
                            noteTv2.setText(superviseAlready);
                        } else if (senderRequest.getResponse().equals(SupervisionRequest.Response.NEUTRAL)) {
                            supRequestBtn.setText(supBtnCancel);
                            //Toast.makeText(ViewAnotherUserActivity.this,
                            // "You currently have a pending request to supervise this user",
                            //Toast.LENGTH_SHORT).show();
                            noteTv2.setText(canCancel);
                            aRequestAlready = true;
                            shouldCancelRequest = true;
                            requestUID = senderRequest.getuId();
                        }
                    }

                }

            }


        });
    }
}
