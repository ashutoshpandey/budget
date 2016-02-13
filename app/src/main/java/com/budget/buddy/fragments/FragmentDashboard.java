package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budget.buddy.data.Utility;

import com.budget.buddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentDashboard extends Fragment{

    private TextView tvTotalBudgets;
    private TextView tvTotalSharings;

    @Override
    public void onResume(){
        super.onResume();

        setBudgetCount();
        setBudgetShareCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_dashboard, container, false);

        tvTotalBudgets = (TextView)rootView.findViewById(R.id.tvTotalBudgets);
        tvTotalSharings = (TextView)rootView.findViewById(R.id.tvTotalSharings);

        return rootView;
    }

    public void setBudgetCount(){

        if(Utility.budgets.size()==0 || (Utility.budgets.containsKey(-1) && Utility.budgets.get(-1).getName().equals("no budgets")))
            tvTotalBudgets.setText("0");
        else
            tvTotalBudgets.setText(String.valueOf(Utility.budgets.size()));
    }

    public void setBudgetShareCount(){
        if(Utility.budgetShares.size()==0 || (Utility.budgetShares.containsKey(-1) && Utility.budgetShares.get(-1).getName().equals("no shares")))
            tvTotalSharings.setText("0");
        else
            tvTotalSharings.setText(String.valueOf(Utility.budgetShares.size()));
    }
}
