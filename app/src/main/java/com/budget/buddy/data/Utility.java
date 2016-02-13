package com.budget.buddy.data;

import android.content.Context;
import android.widget.Toast;

import com.budget.buddy.MainActivity;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
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

/**
 * Created by Ashutosh on 1/14/2016.
 */
public class Utility {
    private static Properties projectProperties;

    public static String customerId;
    public static Customer customer;

    public static String currency = "Rs.";

    public static String server = "http://10.0.2.2/budget/public";
    //public static String server = "http://54.169.114.127/laravel/public/index.php/";

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

    public static String getMonthName(int month) {
        String monthName = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11 ) {
            monthName = months[month];
        }
        return monthName;
    }

    public static void loadBudgets(final Context applicationContext){

        if(Utility.customer==null)
            return;

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budgets", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

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

                ((MainActivity)applicationContext).setBudgetCount();
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

    public static void loadShares(Context applicationContext) {
System.out.println("Loading sharings");
        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-shares", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                HashMap<Integer, BudgetShare> budgetShares = new HashMap<Integer, BudgetShare>();
System.out.println("Shares = " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetShares");

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetShare budgetShare = new BudgetShare();

                            JSONObject withCustomerJSON = null;
                            if(budgetJSON.getJSONObject("to_customer")!=null)
                                withCustomerJSON = budgetJSON.getJSONObject("to_customer");
                            else if(budgetJSON.getJSONObject("from_customer")!=null)
                                withCustomerJSON = budgetJSON.getJSONObject("from_customer");

                            JSONObject sharedBudgetJSON = budgetJSON.getJSONObject("budget");
                            String budgetType = sharedBudgetJSON.getString("budget_type");

                            if(withCustomerJSON!=null){
                                budgetShare.setId(budgetJSON.getInt("id"));
                                budgetShare.setName(withCustomerJSON.getString("name"));
                                budgetShare.setBudgetType(budgetType);
                                budgetShares.put(budgetShare.getId(), budgetShare);
                            }
                        }

                        Utility.budgetShares = budgetShares;

                    } else if (obj.getString("message").equals("empty")) {
                        BudgetShare budgetShare = new BudgetShare();

                        budgetShare.setId(-1);
                        budgetShare.setName("no shares");

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
}
