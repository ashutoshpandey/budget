package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.budget.buddy.R;
import com.budget.buddy.SingleBudgetActivity;
import com.budget.buddy.SingleBudgetDetailActivity;
import com.budget.buddy.adapter.BudgetAdapter;
import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetItem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudgetItem extends Fragment{

    private ListView listView;
    private BudgetItemAdapter adapter;

    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();

    @Override
    public void onResume(){
        super.onResume();
        refreshBudgetItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budget_item, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewBudgetItem);

        adapter = new BudgetItemAdapter(getActivity(), budgetItems);

        listView.setAdapter(adapter);

        refreshBudgetItems();

        return rootView;
    }

    public void setBudgetItems(ArrayList<BudgetItem> budgetItemsPassed){

        budgetItems.clear();

        for(BudgetItem budgetItem : budgetItemsPassed)
            budgetItems.add(budgetItem);
    }

    public void refreshBudgetItems() {

        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
