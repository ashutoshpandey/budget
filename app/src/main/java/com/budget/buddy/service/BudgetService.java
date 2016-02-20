package com.budget.buddy.service;

import android.widget.Toast;

import com.budget.buddy.SingleBudgetDetailActivity;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.pojo.Customer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Ashutosh on 2/19/2016.
 */
public class BudgetService {

    public void loadBudgets(){

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

    public void loadShares() {

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

                        Utility.budgetShares = Utility.budgetShares;

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

    public void removeBudgetItem(int id) {

        final RequestParams params = new RequestParams();

        params.put("item_id", String.valueOf(id));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/remove-item", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if(obj.has("message")) {
                        System.out.println("Budget item removed");
                        loadBudgetItems();
                    }

                } catch (JSONException e) {
                    SingleBudgetDetailActivity.me().budgetItemRemoved("error");
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    SingleBudgetDetailActivity.me().budgetItemRemoved("404");
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    SingleBudgetDetailActivity.me().budgetItemRemoved("500");
                }
                // When Http response code other than 404, 500
                else {
                    SingleBudgetDetailActivity.me().budgetItemRemoved("connectivity");
                }
            }
        });
    }

    public void loadBudgetItems() {

        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        final String yearMonth = year + "," + month;

        final int budgetId = Utility.getCurrentBudget().getId();

        RequestParams params = new RequestParams();

        params.put("budget_id", String.valueOf(budgetId));
        params.put("yearMonth", yearMonth);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-items-filtered", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                ArrayList<BudgetItem> budgetItems = new ArrayList<BudgetItem>();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetItems");

                        double currentAmount = 0;

                        budgetItems.clear();

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetItem budgetItem = new BudgetItem();

                            budgetItem.setId(budgetJSON.getInt("id"));
                            budgetItem.setName(budgetJSON.getString("name"));
                            budgetItem.setPrice(budgetJSON.getDouble("price"));
                            budgetItem.setPaymentMode(budgetJSON.getString("payment_mode"));

                            String createDate;
                            try {
                                createDate = new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(budgetJSON.getString("entry_date")));
                            }
                            catch(Exception ex){
                                createDate = budgetJSON.getString("entry_date");
                            }
                            budgetItem.setCreatedAt(createDate);

                            JSONObject customerJSON = budgetJSON.getJSONObject("customer");
                            budgetItem.setPersonName(customerJSON.getString("name"));

                            if(budgetJSON.has("category") && !budgetJSON.isNull("category")) {
                                JSONObject categoryJSON = budgetJSON.getJSONObject("category");
                                budgetItem.setCategoryName(categoryJSON.getString("name"));
                            }
                            else
                                budgetItem.setCategoryName("Uncategorized");

                            currentAmount += budgetJSON.getDouble("price");

                            budgetItems.add(budgetItem);
                        }

                        SingleBudgetDetailActivity.me().updateBudgetItems(budgetItems, currentAmount, "found");

                    } else if (obj.getString("message").equals("empty")) {
                        SingleBudgetDetailActivity.me().updateBudgetItems(budgetItems, 0, "found");
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
                    SingleBudgetDetailActivity.me().updateBudgetItems(null, 0, "error");
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    SingleBudgetDetailActivity.me().updateBudgetItems(null, 0, "error");
                }
                // When Http response code other than 404, 500
                else {
                    SingleBudgetDetailActivity.me().updateBudgetItems(null, 0, "error");
                }
            }
        });

    }
}
