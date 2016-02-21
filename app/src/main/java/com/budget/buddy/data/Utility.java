package com.budget.buddy.data;

import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.pojo.Category;
import com.budget.buddy.pojo.Customer;
import com.budget.buddy.pojo.PaymentMode;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Ashutosh on 1/14/2016.
 */
public class Utility {
    private static Properties projectProperties;

    public static String customerId;
    public static Customer customer;

    public static int paymentModeCount;
    public static int categoryCount;

    public static String currency = "Rs.";

    //public static String server = "http://10.0.2.2/budget/public";
    public static String server = "http://54.169.114.127/laravel/public/index.php/";

    public static int currentBudgetId;
    public static int currentSharedBudgetId;
    public static String currentBudgetType;

    public static Map<Integer,Budget> budgets = new HashMap<Integer,Budget>();
    public static Map<Integer,BudgetShare> budgetShares = new HashMap<Integer,BudgetShare>();
    public static Map<Integer,Category> categories = new HashMap<Integer,Category>();
    public static Map<Integer,PaymentMode> paymentModes = new HashMap<Integer,PaymentMode>();

    public static int currentDisplayView = 0;

    public static int lastTab = 0;
    public static double currentBudgetCurrentAmount;

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

    public static Budget getCurrentBudget(){
        if(currentBudgetType!=null){

            if(currentBudgetType.equals("created"))
                return Utility.budgets.get(Utility.currentBudgetId);
            else
                return Utility.budgetShares.get(Utility.currentSharedBudgetId).getBudget();
        }
        else
            return null;
    }

    public static String getData(String key){

        if(projectProperties.isEmpty())
            return null;

        if(projectProperties.containsKey(key))
            return projectProperties.getProperty(key);
        else
            return null;
    }

    public static String getMonthName(int month) {
        String monthName = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11 ) {
            monthName = months[month];
        }
        return monthName;
    }
}
