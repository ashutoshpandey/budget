package com.budget.buddy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.budget.buddy.R;
import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.BudgetItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BudgetHistoryDetailsActivity extends Activity {

    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();

    private ListView listView;

    private BudgetItemAdapter adapter;

    private String yearMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_history_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                yearMonth = null;
            } else {
                yearMonth= extras.getString("yearMonth");
            }
        } else {
            yearMonth = (String) savedInstanceState.getSerializable("yearMonth");
        }

        listView = (ListView)findViewById(R.id.lvBudgetHistoryItems);

        adapter = new BudgetItemAdapter(this, budgetItems);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        loadBudgetItems();
    }

    private void loadBudgetItems() {

        RequestParams params = new RequestParams();
        params.put("budget_id", String.valueOf(Utility.currentBudgetId));
        params.put("yearMonth", yearMonth);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-items-filtered", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                System.out.println("Filtered = " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetItems");

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetItem budgetItem = new BudgetItem();

                            budgetItem.setId(budgetJSON.getInt("id"));
                            budgetItem.setName(budgetJSON.getString("name"));
                            budgetItem.setPrice(budgetJSON.getDouble("price"));

                            String createDate;
                            try {
                                createDate = new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-mm-dd").parse(budgetJSON.getString("entry_date")));
                            } catch (Exception ex) {
                                createDate = budgetJSON.getString("entry_date");
                            }
                            budgetItem.setCreatedAt(createDate);

                            JSONObject customerJSON = budgetJSON.getJSONObject("customer");

                            budgetItem.setPersonName(customerJSON.getString("name"));

                            budgetItems.add(budgetItem);
                        }

                        adapter.notifyDataSetChanged();

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
