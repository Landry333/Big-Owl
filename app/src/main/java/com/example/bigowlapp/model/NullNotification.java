package com.example.bigowlapp.model;

import com.google.firebase.firestore.Exclude;

public class NullNotification extends Notification {

    public NullNotification() {
        super(Type.INVALID);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return false;
    }
}
