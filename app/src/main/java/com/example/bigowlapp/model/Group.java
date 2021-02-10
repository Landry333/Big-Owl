package com.example.bigowlapp.model;


import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

// Exclude uneeded data from documents
@IgnoreExtraProperties
public class Group extends Model {

    private String name;
    private String supervisorId;
    private List<String> memberIdList;

    public Group() {
        super();
        memberIdList = new ArrayList<>();
    }

    public Group(String uid, String name) {
        super(uid);
        this.name = name;
    }

    public Group(String uid, String name, String supervisorId, List<String> memberIdList) {
        super(uid);
        this.name = name;
        this.supervisorId = supervisorId;
        this.memberIdList = memberIdList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public List<String> getMemberIdList() {
        return (memberIdList == null) ? new ArrayList<>() : memberIdList;
    }

    public void setMemberIdList(List<String> memberIdList) {
        this.memberIdList = memberIdList;
    }

}