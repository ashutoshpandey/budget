package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.budget.buddy.R;


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

        loadCustomerDataFromFile();
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

    private void loadCustomerDataFromFile() {
        File sdcard = Environment.getExternalStorageDirectory();

        File file = new File(sdcard,"budget.dat");

        if(file.exists()) {

            DataInputStream din = null;
            try {
                din = new DataInputStream(new FileInputStream(file));

                Utility.customerId = din.readUTF();
            } catch (Exception e) {

            }
            finally{
                try{
                    din.close();

                    loadCustomerDataFromServer();
                }
                catch(Exception ex){
                }
            }
        }
        else{
            Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(i);
        }
    }

    private void loadCustomerDataFromServer() {

        RequestParams params = new RequestParams();
        params.put("id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/get-customer-info", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
System.out.println(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONObject customer = obj.getJSONObject("customer");

                        String id = customer.getString("id");
                        String name = customer.getString("name");
                        String phone = customer.getString("phone");
                        String photo = customer.getString("photo");
                        String status = customer.getString("status");
                        String createdAt = customer.getString("created_at");

                        Utility.paymentModeCount = obj.getInt("payment_mode_count");
                        Utility.categoryCount = obj.getInt("category_count");

                        Utility.customer = new Customer(id, name, phone, photo, status, createdAt);

                        //Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
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
                    Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Something went wrong at server", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
