package com.example.bigowlapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    ImageView imgUserAvatar;
    TextView textEmail, textFirstName, textLastName, textPhone, textImageURL;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize();
    }

    protected void initialize()
    {
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference docRef = db.collection("users").document(currentUserUid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                imgUserAvatar = findViewById(R.id.imageView_userAvatar);
                                textEmail = findViewById(R.id.textView_email);
                                textFirstName = findViewById(R.id.textView_firstName);
                                textLastName = findViewById(R.id.textView_lastName);
                                textPhone = findViewById(R.id.textView_phoneNumber);
                                textImageURL = findViewById(R.id.textView_imageURL);

                                Picasso.get().load((String) document.getString("profileImage")).into(imgUserAvatar);

                                textEmail.setText((String) document.getString("email"));
                                textFirstName.setText((String) document.getString("firstName"));
                                textLastName.setText((String) document.getString("lastName"));
                                textPhone.setText((String) document.getString("phoneNumber"));
                                textImageURL.setText((String) document.getString("profileImage"));
                            }
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}