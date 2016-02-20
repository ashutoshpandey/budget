package com.budget.buddy.pojo;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/20/2016.
 */
public class AmountItemList {
    private double amount;
    private ArrayList<BudgetItem> items;

    public AmountItemList(){
    }

    public AmountItemList(ArrayList<BudgetItem> items, double amount){
        this.amount = amount;
        this.items = items;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ArrayList<BudgetItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<BudgetItem> items) {
        this.items = items;
    }
}
