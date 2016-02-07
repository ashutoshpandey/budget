package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budget.buddy.data.Utility;

import buddy.budget.com.budgetbuddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentDashboard extends Fragment{

    private TextView tvTotalBudgets;
    private TextView tvTotalSharings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_dashboard, container, false);

        tvTotalBudgets = (TextView)rootView.findViewById(R.id.tvTotalBudgets);
        tvTotalSharings = (TextView)rootView.findViewById(R.id.tvTotalSharings);

        return rootView;
    }

    public void setBudgetCount(){
        System.out.println("Setting label in dashboard");
        tvTotalBudgets.setText(String.valueOf(Utility.budgets.size()));
    }
}
