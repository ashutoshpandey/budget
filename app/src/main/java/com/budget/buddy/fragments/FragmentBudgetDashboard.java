package com.budget.buddy.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.budget.buddy.AddBudgetItemActivity;
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

    private ImageView imgViewAddItem;

    @Override
    public void onResume(){
        super.onResume();

        refreshDashboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budget_dashboard, container, false);

        tvBudgetAmount = (TextView)rootView.findViewById(R.id.tvBudgetAmount);
        tvCurrentAmount = (TextView)rootView.findViewById(R.id.tvCurrentAmount);
        tvBudgetName = (TextView)rootView.findViewById(R.id.tvBudgetName);
        tvBudgetDurationValue = (TextView)rootView.findViewById(R.id.tvBudgetDurationValue);

        imgViewAddItem = (ImageView)rootView.findViewById(R.id.imgViewAddItem);

        imgViewAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddBudgetItemActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }

    public void refreshDashboard() {

        Budget budget = Utility.getCurrentBudget();

        String name = budget.getName();
        String duration = budget.getDuration();
        double currentAmount = Utility.currentBudgetCurrentAmount;
        double maxAmount = budget.getMaxAmount();

        name = name.substring(0,1).toUpperCase() + name.substring(1);

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
