package com.example.bigowlapp.Model;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

// Exclude uneeded data from documents
@IgnoreExtraProperties
public class Group {

    @DocumentId
    private String uId;
    private String name;
    private String monitorUserId;
    private List<String> supervisedUserId;

    public Group() {

    }

    public Group(String uId, String name, String monitorUserId, List<String> supervisedUserId) {
        this.uId = uId;
        this.name = name;
        this.monitorUserId = monitorUserId;
        this.supervisedUserId = supervisedUserId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonitorUserId() {
        return monitorUserId;
    }

    public void setMonitorUserId(String monitorUserId) {
        this.monitorUserId = monitorUserId;
    }

    public List<String> getSupervisedUserId() {
        return supervisedUserId;
    }

    public void setSupervisedUserId(List<String> supervisedUserId) {
        this.supervisedUserId = supervisedUserId;
    }

}