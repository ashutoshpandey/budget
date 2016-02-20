package com.budget.buddy.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.budget.buddy.R;
import com.budget.buddy.adapter.BudgetAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudgetDashboard extends Fragment{

    private TextView tvBudgetAmount;
    private TextView tvCurrentAmount;
    private TextView tvBudgetName;
    private TextView tvBudgetDurationValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budget_dashboard, container, false);

        tvBudgetAmount = (TextView)rootView.findViewById(R.id.tvBudgetAmount);
        tvCurrentAmount = (TextView)rootView.findViewById(R.id.tvCurrentAmount);
        tvBudgetName = (TextView)rootView.findViewById(R.id.tvBudgetName);
        tvBudgetDurationValue = (TextView)rootView.findViewById(R.id.tvBudgetDurationValue);

        return rootView;
    }

    public void setData(String name, String duration, double currentAmount, double maxAmount) {

        tvBudgetName.setText(name);
        tvBudgetAmount.setText(Utility.currency + " " + maxAmount);
        tvBudgetDurationValue.setText(duration);

        if (currentAmount <= maxAmount)
            tvCurrentAmount.setTextColor(Color.rgb(0, 200, 0));
        else
            tvCurrentAmount.setTextColor(Color.RED);

        if (currentAmount==0)
            tvCurrentAmount.setText(Utility.currency + " 0");
        else
            tvCurrentAmount.setText(Utility.currency + " " + String.valueOf((int) currentAmount));
    }
}
