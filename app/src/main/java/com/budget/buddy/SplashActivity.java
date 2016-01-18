package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.budget.buddy.data.TelephonyInfo;
import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

    private void openApp(){
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loadCustomerDataFromFile(){
        try {
            File budgetFile = new File(getFilesDir().getAbsolutePath() + "/budget.data");

            if(budgetFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(budgetFile));
                String data = br.readLine();

                try {

                } catch (Exception ex) {

                }
            }
            else{
                Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(i);
            }
            // Set TextView text here using tv.setText(s);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerDataFromServer() {
System.out.println("loading from server");
        RequestParams params = new RequestParams();
        params.put("id", 1);

        AsyncHttpClient client = new AsyncHttpClient();
        //client.get(Utility.getData("site_url") + "/get-customer-info", params, new AsyncHttpResponseHandler() {
        client.get("http://budget.dev" + "/get-customer-info", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println("Customer info = " + response);
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
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
