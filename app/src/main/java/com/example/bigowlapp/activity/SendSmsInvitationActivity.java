package com.example.bigowlapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.SmsInvitationRequest;
import com.example.bigowlapp.repository.RepositoryFacade;
import com.example.bigowlapp.repository.SmsInvitationRepository;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.google.i18n.phonenumbers.NumberParseException;

public class SendSmsInvitationActivity extends BigOwlActivity {

    private EditText number, message;
    private Button send;
    private Button cancel;
    private String smsNumber, smsMessage, noteText;
    private TextView noteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteTv = findViewById(R.id.note);

        String contactDetails = getIntent().getStringExtra("contactDetails");
        String contactNumber = getIntent().getStringExtra("contactNumber");

        noteText = "Contact: " + contactDetails + " is not yet registered to the application. Send her/him the invitation below by text sms";

        noteTv.setText(noteText);

        number = findViewById(R.id.number);
        number.setText(contactNumber);

        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        cancel = findViewById(R.id.cancel_sms_invitation);

        initialize();
    }

    protected void initialize() {
        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(SendSmsInvitationActivity.this, AddUsersActivity.class);
            startActivity(intent);
            finish();
        });

        try {
            send.setOnClickListener(view -> {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    beginSendingProcess();
                } else {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_send_sms_invitation;
    }

    private void sendInvitation() {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(smsNumber, null, smsMessage, null, null);
            smsInvitationRequest(smsNumber);
            Toast.makeText(this, "Invitation sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error, Message not sent", Toast.LENGTH_SHORT).show();
        }
    }

    private void beginSendingProcess() {
        smsNumber = number.getText().toString().trim();
        smsMessage = message.getText().toString().trim();

        if (smsMessage.isEmpty() && smsNumber.isEmpty()) {
            Toast.makeText(SendSmsInvitationActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
        } else if (smsNumber.isEmpty()) {
            number.setError("Please enter a phone number");
            number.requestFocus();
        } else if (smsMessage.isEmpty()) {
            message.setError("Please enter a message");
            message.requestFocus();
        } else {
            sendInvitation();
            Intent intent = new Intent(SendSmsInvitationActivity.this, InvitationConfirmationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void smsInvitationRequest(String number){
        RepositoryFacade repositoryFacade = RepositoryFacade.getInstance();
        SmsInvitationRepository smsInvitationRepository = repositoryFacade.getSmsInvitationRepository();
        try{
            number = new PhoneNumberFormatter(this).formatNumber(number);

        }catch (NumberParseException e) {
            Log.e("Formatting Error: ", e.getMessage());
            return;
        }

        SmsInvitationRequest smsInvitationRequest = new SmsInvitationRequest();
        smsInvitationRequest.setPhoneNumberSent(number);
        smsInvitationRequest.setSenderUid(repositoryFacade.getCurrentUserUid());

        smsInvitationRepository.addDocument(smsInvitationRequest);
    }
}