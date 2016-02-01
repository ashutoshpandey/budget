package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.pojo.Customer;
import com.budget.buddy.data.TelephonyInfo;
import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddy.budget.com.budgetbuddy.R;


public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

/*
        new Handler().postDelayed(new Runnable() {

            */
/*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             *//*


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
*/

        loadCustomerDataFromServer();
    }

    public void checkSIM(){

        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        if(isSIM1Ready && isSIM2Ready){
            Toast.makeText(this, "Both sims ready", Toast.LENGTH_LONG);
        }
        else if(isSIM1Ready){
            Toast.makeText(this, "First sim ready", Toast.LENGTH_LONG);
        }
        else if(isSIM2Ready){
            Toast.makeText(this, "Second sim ready", Toast.LENGTH_LONG);
        }
        else{
            Toast.makeText(this, "No sim ready", Toast.LENGTH_LONG);
        }

    }

    private void loadCustomerDataFromServer() {

        RequestParams params = new RequestParams();
        params.put("id", String.valueOf(1));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/get-customer-info", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray customerArray = obj.getJSONArray("customer");
                        JSONObject customer = customerArray.getJSONObject(0);

                        String id = customer.getString("id");
                        String name = customer.getString("name");
                        String phone = customer.getString("photo");
                        String photo = customer.getString("photo");
                        String status = customer.getString("status");
                        String createdAt = customer.getString("created_at");

                        Utility.customer = new Customer(id, name, phone, photo, status, createdAt);

                        loadCustomerBudgetsFromServer();
                        loadCustomerBudgetSharesFromServer();

                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else if (obj.getString("message").equals("empty")) {
                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(i);
                    }
                    else {
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

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customer.getId()));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budgets", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetsArray = obj.getJSONArray("budgets");

                        ArrayList<Budget> budgets = new ArrayList<Budget>();

                        for(int i=0;i<budgetsArray.length();i++){
                            JSONObject budgetJSON = budgetsArray.getJSONObject(i);

                            Budget budget = new Budget();

                            budget.setId(budgetJSON.getInt("id"));
                            budget.setName(budgetJSON.getString("name"));

                            budgets.add(budget);
                        }

                        Utility.budgets = budgets;
                    }
                    else if (obj.getString("message").equals("empty")) {
                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(i);
                    }
                    else {
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

    private void loadCustomerBudgetSharesFromServer() {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customer.getId()));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-shares", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetShares");

                        ArrayList<BudgetShare> budgetShares = new ArrayList<BudgetShare>();

                        for(int i=0;i<budgetSharesArray.length();i++){
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetShare budgetShare = new BudgetShare();

                            budgetShare.setId(budgetJSON.getInt("id"));
                            budgetShare.setName(budgetJSON.getString("name"));

                            budgetShares.add(budgetShare);
                        }

                        Utility.budgetShares = budgetShares;
                    }
                    else if (obj.getString("message").equals("empty")) {
                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(i);
                    }
                    else {
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
}
