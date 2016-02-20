package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budget.buddy.HomeActivity;
import com.budget.buddy.data.Utility;

import com.budget.buddy.R;
import com.budget.buddy.service.PaymentModeService;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentDashboard extends Fragment{

    private TextView tvCountBudgets;
    private TextView tvCountBudgetShares;
    private TextView tvCountCategories;
    private TextView tvCountPaymentModes;

    private TextView tvCountBudgetsLabel;
    private TextView tvCountBudgetSharesLabel;
    private TextView tvCountCategoriesLabel;
    private TextView tvCountPaymentModesLabel;

    @Override
    public void onResume(){
        super.onResume();

        setBudgetCount();
        setBudgetShareCount();
        setCategoryCount();
        setPaymentModeCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvCountBudgets = (TextView)rootView.findViewById(R.id.tvTotalBudgets);
        tvCountBudgetShares = (TextView)rootView.findViewById(R.id.tvTotalSharings);
        tvCountCategories = (TextView)rootView.findViewById(R.id.tvCountCategories);
        tvCountPaymentModes = (TextView)rootView.findViewById(R.id.tvCountPaymentModes);

        tvCountBudgetsLabel = (TextView)rootView.findViewById(R.id.tvCountBudgetsLabel);
        tvCountBudgetSharesLabel = (TextView)rootView.findViewById(R.id.tvCountBudgetSharesLabel);
        tvCountCategoriesLabel = (TextView)rootView.findViewById(R.id.tvCountCategoriesLabel);
        tvCountPaymentModesLabel = (TextView)rootView.findViewById(R.id.tvCountPaymentModesLabel);

        tvCountCategories.setText(String.valueOf(Utility.categoryCount));
        tvCountPaymentModes.setText(String.valueOf(Utility.paymentModeCount));

        initializeEvents();

        return rootView;
    }

    public void initializeEvents() {
        tvCountBudgetsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.me().displayView(1);
            }
        });
        tvCountBudgetSharesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.me().displayView(2);
            }
        });
        tvCountCategoriesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.me().displayView(3);
            }
        });
        tvCountPaymentModesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.me().displayView(4);
            }
        });
    }

    public void setBudgetCount(){

        if(Utility.budgets==null || Utility.budgets.size()==0 || (Utility.budgets.containsKey(-1) && Utility.budgets.get(-1).getName().equals("no budgets")))
            tvCountBudgets.setText("0");
        else
            tvCountBudgets.setText(String.valueOf(Utility.budgets.size()));
    }

    public void setBudgetShareCount(){

        if(Utility.budgetShares==null || Utility.budgetShares.size()==0 || (Utility.budgetShares.containsKey(-1) && Utility.budgetShares.get(-1).getText().equals("no shares")))
            tvCountBudgetShares.setText("0");
        else
            tvCountBudgetShares.setText(String.valueOf(Utility.budgetShares.size()));
    }

    public void setCategoryCount(){
        tvCountPaymentModes.setText(String.valueOf(Utility.categoryCount));
    }

    public void setPaymentModeCount(){
        tvCountPaymentModes.setText(String.valueOf(Utility.paymentModeCount));
    }
}
