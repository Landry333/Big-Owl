package model;


import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

// Exclude public (unwanted) properties to be stored in DB
@IgnoreExtraProperties
public class Group {

    private String id;
    private String name;
    private String monitorUserId;
    private List<String> supervisedUserId;

    public Group() {

    }

    public Group(String id, String name, String monitorUserId, List<String> supervisedUserId) {
        this.id = id;
        this.name = name;
        this.monitorUserId = monitorUserId;
        this.supervisedUserId = supervisedUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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