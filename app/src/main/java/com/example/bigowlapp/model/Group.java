package com.example.bigowlapp.model;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

// Exclude uneeded data from documents
@IgnoreExtraProperties
public class Group {

    @DocumentId
    private String uId;
    private String name;
    private String monitoringUserId;
    private List<String> supervisedUserId;

    public Group() {

    }

    public Group(String uId, String name, String monitoringUserId, List<String> supervisedUserId) {
        this.uId = uId;
        this.name = name;
        this.monitoringUserId = monitoringUserId;
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

    public String getMonitoringUserId() {
        return monitoringUserId;
    }

    public void setMonitoringUserId(String monitoringUserId) {
        this.monitoringUserId = monitoringUserId;
    }

    public List<String> getSupervisedUserId() {
        return supervisedUserId;
    }

    public void setSupervisedUserId(List<String> supervisedUserId) {
        this.supervisedUserId = supervisedUserId;
    }

}