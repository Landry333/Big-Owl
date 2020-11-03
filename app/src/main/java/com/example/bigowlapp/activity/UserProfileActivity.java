package com.example.bigowlapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    ImageView imgUserAvatar;
    TextView textEmail, textFirstName, textLastName, textPhone, textImageURL;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initialize();
    }

    protected void initialize() {
        constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setVisibility(View.GONE);
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference docRef = db.collection("users").document(currentUserUid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                textEmail = findViewById(R.id.textView_email);
                                textFirstName = findViewById(R.id.textView_firstName);
                                textLastName = findViewById(R.id.textView_lastName);
                                textPhone = findViewById(R.id.textView_phoneNumber);
                                textImageURL = findViewById(R.id.textView_imageURL);

                                if (URLUtil.isValidUrl((String) document.getString("profileImage"))) {
                                    imgUserAvatar = findViewById(R.id.imageView_userAvatar);
                                    Picasso.get().load((String) document.getString("profileImage")).into(imgUserAvatar);
                                }

                                textEmail.setText((String) document.getString("email"));
                                textFirstName.setText((String) document.getString("firstName"));
                                textLastName.setText((String) document.getString("lastName"));
                                textPhone.setText((String) document.getString("phoneNumber"));
                                textImageURL.setText((String) document.getString("profileImage"));
                            }
                        }
                        constraintLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

