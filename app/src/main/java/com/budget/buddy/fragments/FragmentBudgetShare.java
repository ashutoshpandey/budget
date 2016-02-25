package com.budget.buddy.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.budget.buddy.HomeActivity;
import com.budget.buddy.SingleBudgetActivity;
import com.budget.buddy.adapter.BudgetShareAdapter;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.data.Utility;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.budget.buddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudgetShare extends Fragment{

    private ListView listView;
    private BudgetShareAdapter adapter;
    private ArrayList<BudgetShare> budgetShares;

    @Override
    public void onResume(){
        super.onResume();
        refreshBudgetShares();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget_shares, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewBudgetShares);

        budgetShares = new ArrayList<>();
        for(Map.Entry<Integer, BudgetShare> entry : Utility.budgetShares.entrySet())
            budgetShares.add(entry.getValue());

        adapter = new BudgetShareAdapter(getActivity(), budgetShares);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                Set<Map.Entry<Integer, Budget>> entries = Utility.budgets.entrySet();

                int i = -1;
                for(Map.Entry<Integer,Budget> entry : entries){
                    ++i;

                    if(i==itemPosition) {
                        Utility.currentBudgetId = entry.getKey();
                        Utility.currentBudgetType = "shared";
                        break;
                    }
                }

                Intent in = new Intent(getActivity(), SingleBudgetActivity.class);
                startActivity(in);
            }

        });
        return rootView;
    }

    public void refreshBudgetShares() {
        if(adapter!=null) {

            budgetShares.clear();
            for(Map.Entry<Integer, BudgetShare> entry : Utility.budgetShares.entrySet())
                budgetShares.add(entry.getValue());

            adapter.notifyDataSetChanged();
        }
    }


}
