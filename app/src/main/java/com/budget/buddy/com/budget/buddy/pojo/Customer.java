package com.budget.buddy.com.budget.buddy.pojo;

public class Customer {

    private String id;
    private String name;
    private String phone;
    private String status;
    private String createdAt;
    private String photo;

    public Customer(){}

    public Customer(String id, String name, String phone, String status, String photo, String createdAt){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.photo = photo;
        this.createdAt = createdAt;
    }
}
