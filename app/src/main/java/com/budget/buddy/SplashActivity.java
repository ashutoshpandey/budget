package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.budget.buddy.data.TelephonyInfo;

import buddy.budget.com.budgetbuddy.R;


public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkCustomer();

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
    }

    public void checkCustomer(){

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

        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
    }
}
