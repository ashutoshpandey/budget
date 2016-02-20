package com.budget.buddy;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.budget.buddy.adapter.NavDrawerListAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.fragments.FragmentBudget;
import com.budget.buddy.fragments.FragmentBudgetShare;
import com.budget.buddy.fragments.FragmentCategory;
import com.budget.buddy.fragments.FragmentDashboard;
import com.budget.buddy.fragments.FragmentPayment;
import com.budget.buddy.fragments.FragmentProfile;
import com.budget.buddy.pojo.NavDrawerItem;
import com.budget.buddy.service.BudgetService;
import com.budget.buddy.service.CategoryService;
import com.budget.buddy.service.PaymentModeService;

public class HomeActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private Fragment fragmentDashboard;
    private Fragment fragmentBudget;
    private Fragment fragmentBudgetShare;
    private Fragment fragmentCategory;
    private Fragment fragmentPayment;
    private Fragment fragmentProfile;

    private static HomeActivity self;

    private Timer timer;
    private BudgetTimerTask timerTask;

    private Menu mainMenu;

    private BudgetService budgetService;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private String[] fragmentTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        self = this;

        fragmentDashboard = new FragmentDashboard();
        fragmentBudget = new FragmentBudget();
        fragmentBudgetShare = new FragmentBudgetShare();
        fragmentCategory = new FragmentCategory();
        fragmentPayment = new FragmentPayment();
        fragmentProfile = new FragmentProfile();

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        fragmentTitles = getResources().getStringArray(R.array.fragment_titles);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        int ct = -1;
        for(String navTitle : navMenuTitles)
            navDrawerItems.add(new NavDrawerItem(navTitle, navMenuIcons.getResourceId(++ct, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        timer = new Timer();
        timerTask = new BudgetTimerTask();
        startTimer();

        budgetService = new BudgetService();

        loadCustomerBudgetsFromServer();
        loadCustomerBudgetSharesFromServer();
    }

    public static HomeActivity me(){
        return self;
    }

    @Override
    public void onBackPressed() {

        if(Utility.currentDisplayView==0) {

            new AlertDialog.Builder(this)
                    .setMessage("Do you want to close the app?")
                    .setTitle("Close?")
                    .setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                                timerTask = null;
                            }

                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                        }
                    })
                    .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        }
        else
            displayView(0);

        return;
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        mainMenu = menu;
        mainMenu.findItem(R.id.action_create_new_budget).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_create_new_budget:
                Intent i = new Intent(HomeActivity.this, NewBudgetActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    public void displayView(int position) {

        Utility.currentDisplayView = position;

        if(mainMenu!=null && mainMenu.findItem(R.id.action_create_new_budget)!=null)
            mainMenu.findItem(R.id.action_create_new_budget).setVisible(false);

        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = fragmentDashboard;
                break;
            case 1:
                fragment = fragmentBudget;

                if(mainMenu!=null && mainMenu.findItem(R.id.action_create_new_budget)!=null)
                    mainMenu.findItem(R.id.action_create_new_budget).setVisible(true);

                break;
            case 2:
                fragment = fragmentBudgetShare;
                break;
            case 3:
                fragment = fragmentCategory;
                break;
            case 4:
                fragment = fragmentPayment;
                break;
            case 5:
                fragment = fragmentProfile;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(fragmentTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /***************** custom methods **********************/

    public void startTimer(){
        timer.schedule(timerTask, 2000, 60000);
    }

    private void createBudget() {
        Intent i = new Intent(HomeActivity.this, NewBudgetActivity.class);
        startActivity(i);
    }

    private void profile() {
        Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    private void logout(){
        finish();
    }

    private void loadCustomerBudgetSharesFromServer() {
        budgetService.loadShares();
    }

    private void loadCustomerBudgetsFromServer() {
        budgetService.loadBudgets();
    }

    public void setBudgetCount() {
        ((FragmentDashboard)fragmentDashboard).setBudgetCount();
        ((FragmentDashboard)fragmentDashboard).setBudgetShareCount();
        ((FragmentBudget)fragmentBudget).refreshBudgets();
        ((FragmentBudgetShare)fragmentBudgetShare).refreshBudgets();
    }

    public void setPaymentCount(int count) {
        ((FragmentDashboard)fragmentDashboard).setPaymentModeCount();
    }

    public void openSingleBudget() {
        Intent i = new Intent(HomeActivity.this, SingleBudgetDetailActivity.class);
        startActivity(i);
    }

    class BudgetTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {

                    budgetService.loadBudgets();
                    budgetService.loadShares();

                    setBudgetCount();
                }});
        }
    }
}
