package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private PaymentModeService paymentModeService;

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

        tvCountCategories.setText(String.valueOf(Utility.categoryCount));

        paymentModeService = new PaymentModeService();
        //paymentModeService.loadPaymentModes(FragmentPayment.this);

        tvCountPaymentModes.setText(String.valueOf(Utility.paymentModeCount));

        return rootView;
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
