package com.budget.buddy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.budget.buddy.R;

public class MainActivity extends Activity {

    ActionBar.Tab Tab1, Tab2, Tab3;
    Fragment fragmentDashboard = new FragmentDashboard();
    Fragment fragmentBudget = new FragmentBudget();
    Fragment fragmentBudgetShare = new FragmentBudgetShare();

    private Timer timer;
    private BudgetTimerTask timerTask;

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

        timer = new Timer();
        timerTask = new BudgetTimerTask();
        startTimer();

        loadCustomerBudgetsFromServer();
        loadCustomerBudgetSharesFromServer();
    }

    public void startTimer(){
        timer.schedule(timerTask, 60000, 60000);
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

            case R.id.menu_profile:
                profile();
                break;

            case R.id.menu_category:
                categories();
                break;

            case R.id.menu_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void categories() {
        Intent i = new Intent(MainActivity.this, CategoryActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Do you want to close the app?")
                .setTitle("Close?")
                .setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(timer!=null){
                            timer.cancel();
                            timer = null;
                            timerTask = null;
                        }

                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        return;
    }
/***************************************** custom methods ************************************/

    private void createBudget() {
        Intent i = new Intent(MainActivity.this, NewBudgetActivity.class);
        startActivity(i);
    }

    private void profile() {
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    private void logout(){
        finish();
    }

    private void loadCustomerBudgetSharesFromServer() {
        Utility.loadShares();
    }

    private void loadCustomerBudgetsFromServer() {
        Utility.loadBudgets();
    }

    public void setBudgetCount() {
        ((FragmentDashboard)fragmentDashboard).setBudgetCount();
        ((FragmentBudget)fragmentBudget).refreshBudgets();
        ((FragmentBudgetShare)fragmentBudgetShare).refreshBudgets();
    }

    public void setShareCount() {
        ((FragmentDashboard)fragmentDashboard).setBudgetShareCount();
    }

    public void openSingleBudget() {
        Intent i = new Intent(MainActivity.this, SingleBudgetActivity.class);
        startActivity(i);
    }

    class BudgetTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    System.out.println("Loading budgets and shares");
                    Utility.loadBudgets();
                    Utility.loadShares();

                    setShareCount();
                    setBudgetCount();
                }});
        }
    }

}
