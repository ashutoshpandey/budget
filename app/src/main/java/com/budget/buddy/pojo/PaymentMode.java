package com.budget.buddy.pojo;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class PaymentMode {

    private int id;
    private String name;

    public PaymentMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public PaymentMode() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
