package com.budget.buddy.data;

import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.pojo.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Ashutosh on 1/14/2016.
 */
public class Utility {
    private static Properties projectProperties;

    public static String customerId;
    public static Customer customer;

    public static String currency = "Rs.";

    //public static String server = "http://10.0.2.2/budget/public";
    public static String server = "http://54.169.114.127/laravel/public/index.php/";

    public static int currentBudgetId;

    public static Map<Integer,Budget> budgets = new HashMap<Integer,Budget>();
    public static Map<Integer,BudgetShare> budgetShares = new HashMap<Integer,BudgetShare>();

    static{
/*
        projectProperties = new Properties();

        try {
            InputStream in = Utility.class.getClassLoader().getResourceAsStream("settings.properties");
            projectProperties.load(in);
            in.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
*/
    }

    public static String getData(String key){

        if(projectProperties.isEmpty())
            return null;

        if(projectProperties.containsKey(key))
            return projectProperties.getProperty(key);
        else
            return null;
    }
}
