package com.example.bigowlapp.model;

import com.google.firebase.firestore.DocumentId;

public abstract class Model {

    @DocumentId
    private String uId;

    public Model() {
    }

    public Model(String uId) {
        this.uId = uId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
