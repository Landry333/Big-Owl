package com.example.bigowlapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bigowlapp.R;
import com.example.bigowlapp.viewModel.ViewUserViewModel;

import java.util.HashMap;
import java.util.Objects;

public class ViewAnotherUserActivity extends AppCompatActivity {
    String otherUserID = getIntent().getStringExtra("userID");
    String supRequestStatus = "none";
    String supBtn1 = "Send a request to supervise user";
    String supBtn2 = "You are supervising this user";
    String supBtn3 = "Cancel this request to supervise";
    private Button supRequestBtn;
    private ViewUserViewModel viewUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_another_user);

        supRequestBtn = findViewById(R.id.SupRequest);

        viewUserViewModel = new ViewModelProvider(this).get(ViewUserViewModel.class);

        supRequestBtn.setOnClickListener(v -> DoRequest(otherUserID));
    }

    private void DoRequest(String otherUserID) {
        if (supRequestStatus.equals("none")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", "pending");

            viewUserViewModel.getCurrentRequestRef().child(viewUserViewModel.getCurrentUser().getUid()).child(otherUserID)
                    .updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewAnotherUserActivity.this,
                            "You have sent a request to supervise ",
                            Toast.LENGTH_SHORT).show();
                    supRequestStatus = "sent_pending";
                    supRequestBtn.setText(supBtn3);
                } else {
                    Toast.makeText(ViewAnotherUserActivity.this,
                            "" + Objects.requireNonNull(task.getException()).toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (supRequestStatus.equals("sent_pending") || supRequestStatus.equals("sent_declined")) {
            viewUserViewModel.getCurrentRequestRef()
                    .child(viewUserViewModel.getCurrentUser().getUid())
                    .child(otherUserID)
                    .removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewAnotherUserActivity.this,
                                    "You have canceled request to supervise ",
                                    Toast.LENGTH_SHORT).show();
                            supRequestStatus = "none";
                            supRequestBtn.setText(supBtn1);
                        } else {
                            Toast.makeText(ViewAnotherUserActivity.this,
                                    "" + Objects.requireNonNull(task.getException()).toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (supRequestStatus.equals("accepted")) supRequestBtn.setText(supBtn2);
    }
}
