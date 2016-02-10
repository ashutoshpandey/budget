package com.budget.buddy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.budget.buddy.adapter.BudgetHistoryAdapter;
import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetHistory;
import com.budget.buddy.pojo.BudgetItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import com.budget.buddy.R;

public class BudgetHistoryActivity extends Activity {

    private BudgetHistoryAdapter adapter;
    private Budget budget;

    private ListView listView;

    private ArrayList<BudgetHistory> budgetHistories = new ArrayList<>();
    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_history);

        listView = (ListView)findViewById(R.id.lvBudgetHistory);

        adapter = new BudgetHistoryAdapter(this, budgetHistories);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        budget = Utility.budgets.get(Utility.currentBudgetId);

        if(budget!=null)
            loadBudgetItems();
    }

    private void loadBudgetItems() {

        RequestParams params = new RequestParams();
        params.put("budget_id", String.valueOf(Utility.currentBudgetId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-items", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                System.out.println("History = " + response);
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
                            budgetItem.setCreatedAt(budgetJSON.getString("entry_date"));

                            budgetItems.add(budgetItem);
                        }

                        loadHistoryData(budgetItems);

                    } else if (obj.getString("message").equals("empty")) {
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

    private void loadHistoryData(ArrayList<BudgetItem> budgetItems) {

        budgetHistories.clear();

        HashSet<String> yearMonths = new HashSet<>();

        for(BudgetItem item : budgetItems){

            String entryDate = item.getCreatedAt();

            DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(entryDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(date!=null) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                BudgetHistory history = new BudgetHistory();

                String yearMonth = year + "," + month;
                String text = Utility.getMonthName(month) + "-" + year;
                double amount = 0;

                history.setText(text);
                history.setYearMonth(yearMonth);
                history.setAmount(amount);

                budgetHistories.add(history);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
