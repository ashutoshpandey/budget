package com.budget.buddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddy.budget.com.budgetbuddy.R;

public class SingleBudgetActivity extends Activity {

    private TextView tvBudgetAmount;
    private TextView tvCurrentAmount;
    private TextView tvBudgetName;

    private ListView listView;

    private BudgetItemAdapter adapter;
    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_budget);

        tvBudgetAmount = (TextView)findViewById(R.id.tvBudgetAmount);
        tvCurrentAmount = (TextView)findViewById(R.id.tvCurrentAmount);
        tvBudgetName = (TextView)findViewById(R.id.tvBudgetName);

        listView = (ListView)findViewById(R.id.listView);

        adapter = new BudgetItemAdapter(this, budgetItems);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        initializeData();
    }

    private void initializeData() {
        Budget budget = Utility.budgets.get(Utility.currentBudgetId);

        if(budget!=null){

            tvBudgetName.setText(budget.getName());
            tvBudgetAmount.setText(String.valueOf(budget.getMaxAmount()));

            loadBudgetItems();
        }
    }

    private void loadBudgetItems() {

        RequestParams params = new RequestParams();
        params.put("budget_id", String.valueOf(Utility.currentBudgetId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-items", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetItems");

                        double currentAmount = 0;

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetItem budgetItem = new BudgetItem();

                            budgetItem.setId(budgetJSON.getInt("id"));
                            budgetItem.setName(budgetJSON.getString("name"));
                            budgetItem.setPrice(budgetJSON.getDouble("price"));
                            budgetItem.setCreatedAt(budgetJSON.getString("entry_date"));

                            currentAmount += budgetJSON.getDouble("price");

                            budgetItems.add(budgetItem);
                        }

                        tvCurrentAmount.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);

                        tvCurrentAmount.setText(String.valueOf(currentAmount));

                    } else if (obj.getString("message").equals("empty")) {
                        tvCurrentAmount.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
