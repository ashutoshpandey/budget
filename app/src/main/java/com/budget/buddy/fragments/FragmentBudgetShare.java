package com.budget.buddy.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.budget.buddy.SingleBudgetActivity;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.data.Utility;

import java.util.ArrayList;

import buddy.budget.com.budgetbuddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudgetShare extends Fragment{

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_budgets, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewBudgets);

        ArrayList<String> budgetNames = new ArrayList<String>();
        for(BudgetShare budgetShare : Utility.budgetShares)
            budgetNames.add(budgetShare.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, budgetNames);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                //String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent i = new Intent(getActivity(), SingleBudgetActivity.class);
                startActivity(i);

            }

        });
        return rootView;
    }
}
