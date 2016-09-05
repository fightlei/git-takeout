package com.example.cloudhua.enity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by cloudhua on 16-7-28.
 */
public class Order implements Serializable{
    private String id ;
    private List<Product> products ;
    private Team team ;
    private String date ;
    private int state ; //0 等待处理 1 已完成  3已取消

    public Order() {
    }

    public Order(String id , List<Product> products, Team team, String date , int state) {
        this.products = products;
        this.team = team;
        this.date = date;
        this.id = id ;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
