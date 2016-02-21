package com.budget.buddy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.data.Utility;
import com.budget.buddy.fragments.FragmentBudgetCategorize;
import com.budget.buddy.fragments.FragmentBudgetDashboard;
import com.budget.buddy.fragments.FragmentBudgetItem;
import com.budget.buddy.fragments.FragmentBudgetPayment;
import com.budget.buddy.fragments.TabListener;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.service.BudgetService;
import com.budget.buddy.service.CategoryService;

import java.util.ArrayList;

public class SingleBudgetDetailActivity extends Activity {

    ActionBar.Tab tabDetail, tabItems, tabCategorized, tabByPayment;
    Fragment fragmentBudgetDashboard = new FragmentBudgetDashboard();
    Fragment fragmentBudgetItem = new FragmentBudgetItem();
    Fragment fragmentBudgetCategorize = new FragmentBudgetCategorize();
    Fragment fragmentBudgetPayment = new FragmentBudgetPayment();

    private BudgetService budgetService;
    private CategoryService categoryService;

    private static SingleBudgetDetailActivity self;

    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();

    public static SingleBudgetDetailActivity me(){
        return self;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_budget_detail);

        self = this;
        budgetService = new BudgetService();
        categoryService = new CategoryService();

        categoryService.loadCategories(String.valueOf(Utility.customerId), Utility.categories);

        ActionBar actionBar = getActionBar();

        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(false);

        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(false);

        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set Tab Icon and Titles
/*
        tabDetail = actionBar.newTab().setIcon(R.drawable.tab1);
        tabItems = actionBar.newTab().setText("tabItems");
        tabCategorized = actionBar.newTab().setText("tabCategorized");
*/

        tabDetail = actionBar.newTab().setText("Details");
        tabItems = actionBar.newTab().setText("Items");
        tabCategorized = actionBar.newTab().setText("Categorized");
        tabByPayment = actionBar.newTab().setText("By Payments");

        // Set Tab Listeners
        tabDetail.setTabListener(new TabListener(fragmentBudgetDashboard));
        tabItems.setTabListener(new TabListener(fragmentBudgetItem));
        tabCategorized.setTabListener(new TabListener(fragmentBudgetCategorize));
        tabByPayment.setTabListener(new TabListener(fragmentBudgetPayment));

        // Add tabs to actionbar
        actionBar.addTab(tabDetail);
        actionBar.addTab(tabItems);
        actionBar.addTab(tabCategorized);
        actionBar.addTab(tabByPayment);

        loadBudgetItems();
    }

    public void loadBudgetItems(){

        budgetService.loadBudgetItems();
    }

    public void updateBudgetItems(ArrayList<BudgetItem> budgetItems, String message){

        if(message.equals("found")) {
            this.budgetItems = budgetItems;

            ((FragmentBudgetItem) fragmentBudgetItem).setBudgetItems(budgetItems);
            ((FragmentBudgetItem) fragmentBudgetItem).refreshBudgetItems();

            ((FragmentBudgetCategorize) fragmentBudgetCategorize).setBudgetItems(budgetItems);

            ((FragmentBudgetPayment) fragmentBudgetPayment).setBudgetItems(budgetItems);

            ((FragmentBudgetDashboard) fragmentBudgetDashboard).refreshDashboard();
        }
        else
            Toast.makeText(getApplicationContext(), "Cannot load items", Toast.LENGTH_LONG).show();
    }

    public void budgetItemRemoved(String message) {

        if(message.equals("removed"))
            ((FragmentBudgetItem)fragmentBudgetItem).refreshBudgetItems();
        else
            Toast.makeText(getApplicationContext(), "Cannot remove item at this moment", Toast.LENGTH_LONG).show();
    }
}