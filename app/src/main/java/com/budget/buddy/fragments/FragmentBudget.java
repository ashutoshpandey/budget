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

import buddy.budget.com.budgetbuddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudget extends Fragment{

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_budgets, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewBudgets);

        ArrayList<String> budgetNames = new ArrayList<String>();
        for(Map.Entry<Integer, Budget> entry : Utility.budgets.entrySet())
            budgetNames.add(entry.getValue().getName());

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
                int itemPosition = position;

                // ListView Clicked item value
                Set<Map.Entry<Integer, Budget>> entries = Utility.budgets.entrySet();

                int i = -1;
                for(Map.Entry<Integer,Budget> entry : entries){
                    ++i;

                    if(i==itemPosition) {
                        Utility.currentBudgetId = entry.getKey();
                        break;
                    }
                }

                Intent in = new Intent(getActivity(), SingleBudgetActivity.class);
                startActivity(in);
            }

        });
        return rootView;
    }

    private void setBudgetCount() {
        System.out.println("Calling main activity method");
        ((MainActivity)getActivity()).setBudgetCount();
    }
}
