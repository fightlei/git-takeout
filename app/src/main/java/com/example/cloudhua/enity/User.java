package com.example.cloudhua.enity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudhua on 16-7-28.
 */
public class User implements Serializable{
    private String id ;
    private String name ;
    private String number;
    private List<Order> list_order = new ArrayList<Order>() ;

    public User() {
    }
    public User(String id ,String name ,String number) {
        this.name = name;
        this.id = id ;
        this.number = number ;
    }
    public User(String id ,String name, String number, List<Order> list_order) {
        this.name = name;
        this.number = number;
        this.list_order = list_order;
        this.id = id ;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public List<Order> getList_order() {
        return list_order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setList_order(List<Order> list_order) {
        this.list_order = list_order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        User u = (User)o;
        if(id!=null&&u.id!=null){
            if(id.equals(u.id)){
                return true ;
            }else{
                return false;
            }
        }else{
            if(name.equals(u.name)&&number.equals(u.number)){
                return true;
            }else{
                return false ;
            }
        }
    }
}
