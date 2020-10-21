package com.example.bigowlapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendSmsInvitationActivity extends AppCompatActivity {

    private EditText number, message;
    private Button send, testSendRequest;
    private String number2,message2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms_invitation);

        number =findViewById(R.id.number);
        message = findViewById(R.id.message);
        send =findViewById(R.id.send);
        testSendRequest=findViewById(R.id.testSendRequest);
        initialize();
    }

    protected void initialize(){

        try{
            send.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){

                            beginSendingProcess();
                        }
                        else {
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                        }
                    }
                    else beginSendingProcess();

                }
            });


            testSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SendSmsInvitationActivity.this, ViewAnotherUserActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private void sendInvitation(){

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number2, null, message2, null, null);
            Toast.makeText(this, "Invitation sent", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error, Message not sent", Toast.LENGTH_SHORT).show();
        }
    }

    private void beginSendingProcess(){
        number2=number.getText().toString().trim();
        message2=message.getText().toString().trim();

        if(message2.isEmpty() && number2.isEmpty())
        {
            Toast.makeText(SendSmsInvitationActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
        }
        else if(number2.isEmpty())
        {
            //Toast.makeText(SignUpPageActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            number.setError("Please enter a phone number");
            number.requestFocus();
        }
        else if(message2.isEmpty())
        {
            //Toast.makeText(SignUpPageActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            message.setError("Please enter a message");
            message.requestFocus();
        }
        else {
            sendInvitation();
            Intent intent = new Intent( SendSmsInvitationActivity.this, InvitationConfirmationActivity.class);
            startActivity(intent);
            finish();
        }
    }
}