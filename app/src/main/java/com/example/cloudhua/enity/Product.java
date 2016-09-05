package com.example.cloudhua.enity;

import java.io.Serializable;

/**
 * Created by cloudhua on 16-7-29.
 */
public class Product implements Serializable{
    private String id ;
    private String name ;
    private double price ;
    private int count ;

    public Product() {
    }
    public Product(String id ,String name, double price) {
        this.name = name;
        this.price = price;
        this.id = id ;
    }
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public Product(String id , String name, double price, int count ) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.id = id ;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
