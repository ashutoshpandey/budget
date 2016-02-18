package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.budget.buddy.adapter.BudgetAdapter;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.data.Utility;

import java.util.ArrayList;
import java.util.Map;

import com.budget.buddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudget extends Fragment{

    private ListView listView;
    private BudgetAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budgets, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewBudgets);

        ArrayList<Budget> budgets = new ArrayList<>();
        for(Map.Entry<Integer, Budget> entry : Utility.budgets.entrySet())
            budgets.add(entry.getValue());

        adapter = new BudgetAdapter(getActivity(), budgets);

        listView.setAdapter(adapter);

        return rootView;
    }

    public void refreshBudgets() {
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
