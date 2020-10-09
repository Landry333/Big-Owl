package repository;

import com.google.firebase.firestore.FirebaseFirestore;

import database.Firestore;
import model.User;

public class UserRepository {

    private FirebaseFirestore mFirebaseFirestore;

    public UserRepository() {
        mFirebaseFirestore = Firestore.getDatabase();
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return mFirebaseFirestore.collection("users").document(phoneNumber);
    }

}
