package com.example.bigowlapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;


@IgnoreExtraProperties
public class User extends Model implements Parcelable {

    private String firstName;
    private String lastName;
    private String fingerprintAuthRegistration = "NO";
    private String phoneNumber;
    private String email;
    private String profileImage;
    private List<String> memberGroupIdList;

    // TODO: Create a builder (possibly for all models)

    public User() {
        super();
    }

    public User(String uid, String firstName, String lastName, String email) {
        super(uid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String uid, String firstName, String lastName, String phoneNumber, String email,
                String profileImage, List<String> memberGroupIdList) {
        super(uid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profileImage = profileImage;
        this.memberGroupIdList = memberGroupIdList;
    }

    public User(String uid, String firstName, String lastName, String phoneNumber, String email,
                String profileImage, List<String> memberGroupIdList, String fingerprintAuthRegistration) {
        super(uid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profileImage = profileImage;
        this.memberGroupIdList = memberGroupIdList;
        this.fingerprintAuthRegistration = fingerprintAuthRegistration;
    }

    protected User(Parcel in) {
        uid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        fingerprintAuthRegistration = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        profileImage = in.readString();
        memberGroupIdList = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(fingerprintAuthRegistration);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(profileImage);
        dest.writeStringList(memberGroupIdList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getFingerprintAuthRegistration() { return fingerprintAuthRegistration; }

    public void setFingerprintAuthRegistration(String fingerprintAuthRegistration)
    { this.fingerprintAuthRegistration = fingerprintAuthRegistration; }

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

    public List<String> getMemberGroupIdList() {
        return memberGroupIdList;
    }

    public void setMemberGroupIdList(List<String> memberGroupIdList) {
        this.memberGroupIdList = memberGroupIdList;
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

    public static class Field {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String EMAIL = "email";
        public static final String PROFILE_IMAGE = "profileImage";
        public static final String MEMBER_GROUP_ID_LIST = "memberGroupIdList";

        private Field() {
            // constants class should not be instantiated
        }
    }
}
