package com.budget.buddy.pojo;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class BudgetShare {

    private int id;
    private int customerId;

    private String budgetType;
    private String name;

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
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
