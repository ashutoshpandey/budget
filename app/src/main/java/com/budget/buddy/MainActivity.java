package com.budget.buddy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.budget.buddy.data.Utility;
import com.budget.buddy.fragments.FragmentBudget;
import com.budget.buddy.fragments.FragmentBudgetShare;
import com.budget.buddy.fragments.FragmentDashboard;
import com.budget.buddy.fragments.TabListener;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import buddy.budget.com.budgetbuddy.R;

public class MainActivity extends Activity {

    ActionBar.Tab Tab1, Tab2, Tab3;
    Fragment fragmentDashboard = new FragmentDashboard();
    Fragment fragmentBudget = new FragmentBudget();
    Fragment fragmentBudgetShare = new FragmentBudgetShare();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();

        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(false);

        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(false);

        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set Tab Icon and Titles
/*
        Tab1 = actionBar.newTab().setIcon(R.drawable.tab1);
        Tab2 = actionBar.newTab().setText("Tab2");
        Tab3 = actionBar.newTab().setText("Tab3");
*/

        Tab1 = actionBar.newTab().setText("Dashboard");
        Tab2 = actionBar.newTab().setText("Budgets");
        Tab3 = actionBar.newTab().setText("Sharing");

        // Set Tab Listeners
        Tab1.setTabListener(new TabListener(fragmentDashboard));
        Tab2.setTabListener(new TabListener(fragmentBudget));
        Tab3.setTabListener(new TabListener(fragmentBudgetShare));

        // Add tabs to actionbar
        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);

        loadCustomerBudgetsFromServer();
        loadCustomerBudgetSharesFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_new:
                createBudget();
                break;

            case R.id.menu_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

/***************************************** custom methods ************************************/

    private void createBudget() {
        Intent i = new Intent(MainActivity.this, NewBudgetActivity.class);
        startActivity(i);
    }

    private void logout(){
        finish();
    }

    private void loadCustomerBudgetSharesFromServer() {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-shares", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetShares");

                        HashMap<Integer, BudgetShare> budgetShares = new HashMap<Integer, BudgetShare>();

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetShare budgetShare = new BudgetShare();

                            budgetShare.setId(budgetJSON.getInt("id"));
                            budgetShare.setName(budgetJSON.getString("name"));

                            budgetShares.put(budgetShare.getId(), budgetShare);
                        }

                        Utility.budgetShares = budgetShares;
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadCustomerBudgetsFromServer() {

        if(Utility.customer==null)
            return;

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budgets", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetsArray = obj.getJSONArray("budgets");

                        HashMap<Integer, Budget> budgets = new HashMap<Integer, Budget>();

                        for (int i = 0; i < budgetsArray.length(); i++) {
                            JSONObject budgetJSON = budgetsArray.getJSONObject(i);

                            Budget budget = new Budget();

                            budget.setId(budgetJSON.getInt("id"));
                            budget.setName(budgetJSON.getString("name"));
                            budget.setMaxAmount(budgetJSON.getDouble("max_amount"));

                            budgets.put(budget.getId(), budget);
                        }

                        Utility.budgets = budgets;

                        setBudgetCount();

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setBudgetCount() {
        System.out.println("Calling fragment dashboard");
        ((FragmentDashboard)fragmentDashboard).setBudgetCount();
    }
}
