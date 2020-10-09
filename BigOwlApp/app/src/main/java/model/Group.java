package model;


import com.google.firebase.firestore.IgnoreExtraProperties;

// Exclude public (unwanted) properties to be stored in DB
@IgnoreExtraProperties
public class Group {

    private String id;
    private String name;
    private String monitorUserId;
    private String[] supervisedUserId;

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

    public String[] getSupervisedUserId() {
        return supervisedUserId;
    }

    public void setSupervisedUserId(String[] supervisedUserId) {
        this.supervisedUserId = supervisedUserId;
    }
}