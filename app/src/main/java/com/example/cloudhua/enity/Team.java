package com.example.cloudhua.enity;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by cloudhua on 16-7-28.
 */
public class Team implements Serializable{
    private String id ;
    private String name ;
    private User captain;
    private User [] members ;
    public Team(){}

    public Team(String id, String name, User captain, User[] members) {
        this.id = id;
        this.name = name;
        this.captain = captain;
        this.members = members;
    }

    public Team(User captain, String name) {
        this.captain = captain;
        this.name = name;
    }
    public Team(String id , String name ){
        this.id = id ;
        this.name = name ;
    }
    public Team(String id){
        this.id = id ;
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

    public User getCaptain() {
        return captain;
    }

    public void setCaptain(User captain) {
        this.captain = captain;
    }

    public User[] getMembers() {
        return members;
    }

    public void setMembers(User[] members) {
        this.members = members;
    }
}
