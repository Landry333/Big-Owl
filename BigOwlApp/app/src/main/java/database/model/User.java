package database.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

// Exclude public (unwanted) properties to be stored in DB
@IgnoreExtraProperties
@Deprecated
public class User {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String profileImage;

    public User() {
    }

    public User(String firstName, String lastName, String phoneNumber, String email, String profileImage) {
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
}
