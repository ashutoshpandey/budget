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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
