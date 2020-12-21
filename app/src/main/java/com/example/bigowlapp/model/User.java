package com.example.bigowlapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User extends Model {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String profileImage;

    // TODO: Create a builder (possibly for all models)

    public User() {
        super();
    }

    public User(String uId, String firstName, String lastName, String phoneNumber, String email, String profileImage) {
        super(uId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Exclude
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @NonNull
    @Override
    public String toString() {
        return getFullName();
    }

}
