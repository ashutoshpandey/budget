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

import com.budget.buddy.data.Utility;
import com.budget.buddy.fragments.FragmentBudget;
import com.budget.buddy.fragments.FragmentBudgetShare;
import com.budget.buddy.fragments.FragmentDashboard;
import com.budget.buddy.fragments.TabListener;
import com.budget.buddy.pojo.PaymentMode;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    ActionBar.Tab tabDashboard, tabBudgets, tabShares;
    Fragment fragmentDashboard = new FragmentDashboard();
    Fragment fragmentBudget = new FragmentBudget();
    Fragment fragmentBudgetShare = new FragmentBudgetShare();

    private Timer timer;
    private BudgetTimerTask timerTask;

    @Override
    protected  void onResume(){
        super.onResume();

        getActionBar().setSelectedNavigationItem(Utility.lastTab);
    }

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
        tabDashboard = actionBar.newTab().setIcon(R.drawable.tab1);
        tabBudgets = actionBar.newTab().setText("tabBudgets");
        tabShares = actionBar.newTab().setText("tabShares");
*/

        tabDashboard = actionBar.newTab().setText("Dashboard");
        tabBudgets = actionBar.newTab().setText("Budgets");
        tabShares = actionBar.newTab().setText("Sharing");

        // Set Tab Listeners
        tabDashboard.setTabListener(new TabListener(fragmentDashboard));
        tabBudgets.setTabListener(new TabListener(fragmentBudget));
        tabShares.setTabListener(new TabListener(fragmentBudgetShare));

        // Add tabs to actionbar
        actionBar.addTab(tabDashboard);
        actionBar.addTab(tabBudgets);
        actionBar.addTab(tabShares);

        getActionBar().setSelectedNavigationItem(Utility.lastTab);

        timer = new Timer();
        timerTask = new BudgetTimerTask();
        startTimer();

        loadCustomerBudgetsFromServer();
        loadCustomerBudgetSharesFromServer();

        Utility.loadCategories();
    }

    public void startTimer(){
        timer.schedule(timerTask, 2000, 60000);
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

            case R.id.menu_payment_mode:
                paymentModes();
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

    private void paymentModes() {
        Intent i = new Intent(MainActivity.this, PaymentModeActivity.class);
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

                    Utility.loadBudgets();
                    Utility.loadShares();

                    setShareCount();
                    setBudgetCount();
                }});
        }
    }

}
