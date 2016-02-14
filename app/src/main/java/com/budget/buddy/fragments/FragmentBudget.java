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
import android.widget.Toast;

import com.budget.buddy.MainActivity;
import com.budget.buddy.SingleBudgetActivity;
import com.budget.buddy.adapter.BudgetAdapter;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        View rootView = inflater.inflate(R.layout.main_budgets, container, false);

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
