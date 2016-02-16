package com.budget.buddy.data;

import android.content.Context;
import android.widget.Toast;

import com.budget.buddy.MainActivity;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.pojo.Category;
import com.budget.buddy.pojo.Customer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

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
    public static int currentSharedBudgetId;
    public static String currentBudgetType;

    public static Map<Integer,Budget> budgets = new HashMap<Integer,Budget>();
    public static Map<Integer,BudgetShare> budgetShares = new HashMap<Integer,BudgetShare>();

    public static Map<Integer,Category> categories = new HashMap<Integer,Category>();

    public static int lastTab = 0;

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

    public static String getMonthName(int month) {
        String monthName = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11 ) {
            monthName = months[month];
        }
        return monthName;
    }

    public static void loadBudgets(){

        if(Utility.customer==null)
            return;

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budgets", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
System.out.println(response);
                HashMap<Integer, Budget> budgets = new HashMap<Integer, Budget>();
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetsArray = obj.getJSONArray("budgets");

                        for (int i = 0; i < budgetsArray.length(); i++) {
                            JSONObject budgetJSON = budgetsArray.getJSONObject(i);

                            Budget budget = new Budget();

                            String budgetType = budgetJSON.getString("budget_type").toUpperCase();
                            String duration = "N/A";

                            if(budgetType.equals("MONTHLY")) {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
                                String month = dateFormat.format(cal.getTime());
                                int year = cal.get(Calendar.YEAR);

                                duration = month + "-" + year;
                                budget.setDuration(duration);
                            }
                            else {
                                String startDate = budgetJSON.getString("start_date");
                                String endDate = budgetJSON.getString("end_date");

                                duration = startDate + " - " + endDate;
                                budget.setDuration(duration);
                            }

                            budget.setId(budgetJSON.getInt("id"));
                            budget.setCustomerId(budgetJSON.getInt("customer_id"));
                            budget.setName(budgetJSON.getString("name"));
                            budget.setMaxAmount(budgetJSON.getDouble("max_amount"));
                            budget.setBudgetType(budgetType);

                            budgets.put(budget.getId(), budget);
                        }

                        Utility.budgets = budgets;
                    }
                    else if(obj.getString("message").equals("empty")){
                        Budget budget = new Budget();

                        budget.setId(-1);
                        budget.setName("no budgets");

                        budgets.put(budget.getId(), budget);

                        Utility.budgets = budgets;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {

                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                }
                // When Http response code other than 404, 500
                else {

                }
            }
        });
    }

    public static void loadShares() {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-shares", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                HashMap<Integer, BudgetShare> budgetShares = new HashMap<Integer, BudgetShare>();

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetShares");

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetShare budgetShare = new BudgetShare();
                            budgetShare.setId(budgetJSON.getInt("id"));
                            budgetShare.setText("shared");

                            JSONObject withCustomerJSON = null;

                            if(budgetJSON.has("to_customer"))
                                withCustomerJSON = budgetJSON.getJSONObject("to_customer");
                            else if(budgetJSON.has("from_customer"))
                                withCustomerJSON = budgetJSON.getJSONObject("from_customer");

                            JSONObject sharedBudgetJSON = budgetJSON.getJSONObject("budget");

                            if(sharedBudgetJSON!=null) {

                                Budget budget = new Budget();

                                String budgetType = sharedBudgetJSON.getString("budget_type").toUpperCase();
                                budget.setId(sharedBudgetJSON.getInt("id"));
                                budget.setCustomerId(sharedBudgetJSON.getInt("customer_id"));
                                budget.setBudgetType(sharedBudgetJSON.getString("budget_type"));
                                budget.setName(sharedBudgetJSON.getString("name"));
                                budget.setMaxAmount(sharedBudgetJSON.getDouble("max_amount"));
                                String duration = "N/A";

                                if(budgetType.equals("MONTHLY")) {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
                                    String month = dateFormat.format(cal.getTime());
                                    int year = cal.get(Calendar.YEAR);

                                    duration = month + "-" + year;
                                }
                                else {
                                    String startDate = sharedBudgetJSON.getString("start_date");
                                    String endDate = sharedBudgetJSON.getString("end_date");

                                    duration = startDate + " - " + endDate;
                                }

                                budget.setDuration(duration);

                                budgetShare.setBudget(budget);

                            }
                            if(withCustomerJSON!=null){

                                Customer customer = new Customer();

                                customer.setName(withCustomerJSON.getString("name"));

                                budgetShare.setCustomer(customer);
                            }

                            budgetShares.put(budgetShare.getId(), budgetShare);
                        }

                        Utility.budgetShares = budgetShares;

                    } else if (obj.getString("message").equals("empty")) {
                        BudgetShare budgetShare = new BudgetShare();

                        budgetShare.setId(-1);
                        budgetShare.setText("no shares");

                        budgetShares.put(budgetShare.getId(), budgetShare);

                        Utility.budgetShares = budgetShares;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //((MainActivity)applicationContext).setShareCount();
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                System.out.println("Share status = " + statusCode);
                if (statusCode == 404) {
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                }
                // When Http response code other than 404, 500
                else {
                }
            }
        });
    }

    public static void loadCategories() {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-categories", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                HashMap<Integer, BudgetShare> budgetShares = new HashMap<Integer, BudgetShare>();

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("categories");

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            Category category = new Category();
                            category.setId(budgetJSON.getInt("id"));
                            category.setName(budgetJSON.getString("name"));

                            categories.put(category.getId(), category);
                        }

                    } else if (obj.getString("message").equals("empty")) {
                        Category category = new Category();

                        category.setId(-1);
                        category.setName("no categories");

                        categories.put(category.getId(), category);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                System.out.println("Share status = " + statusCode);
                if (statusCode == 404) {
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                }
                // When Http response code other than 404, 500
                else {
                }
            }
        });
    }
}
