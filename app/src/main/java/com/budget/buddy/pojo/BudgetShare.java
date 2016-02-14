package com.budget.buddy.pojo;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class BudgetShare {

    private int id;
    private Customer customer;

    private String text;

    private Budget budget;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer fromCustomer) {
        this.customer = fromCustomer;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}