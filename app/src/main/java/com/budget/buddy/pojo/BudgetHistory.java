package com.budget.buddy.pojo;

/**
 * Created by Ashutosh on 2/10/2016.
 */
public class BudgetHistory {

    private String yearMonth;
    private String text;
    private double amount;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount(){
        return amount;
    }
}
