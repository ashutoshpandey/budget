package com.budget.buddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Customer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.budget.buddy.R;

public class SingleBudgetActivity extends Activity {

    private TextView tvBudgetAmount;
    private TextView tvCurrentAmount;
    private TextView tvBudgetName;
    private TextView tvBudgetDurationValue;
    private TextView tvBudgetItems;
    private ImageButton imgHistory;

    private ListView listView;

    private Budget budget;

    private BudgetItemAdapter adapter;
    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_budget);

        tvBudgetAmount = (TextView)findViewById(R.id.tvBudgetAmount);
        tvCurrentAmount = (TextView)findViewById(R.id.tvCurrentAmount);
        tvBudgetName = (TextView)findViewById(R.id.tvBudgetName);
        tvBudgetItems = (TextView)findViewById(R.id.tvBudgetItems);
        tvBudgetDurationValue = (TextView)findViewById(R.id.tvBudgetDurationValue);
        imgHistory = (ImageButton)findViewById(R.id.imgHistory);

        listView = (ListView)findViewById(R.id.listView);

        adapter = new BudgetItemAdapter(this, budgetItems);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BudgetHistoryActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        initializeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_budget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_new_item:
                addItem();
                break;

            case R.id.menu_edit_budget:
                editBudget();
                break;

            case R.id.menu_share:
                shareBudget();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editBudget() {
        Intent i = new Intent(getApplicationContext(), EditBudgetActivity.class);
        startActivity(i);
    }

    private void shareBudget() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter other person customer id");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();

                shareBudgetNow(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private void shareBudgetNow(String toCustomerId) {
        RequestParams params = new RequestParams();
        params.put("to_customer_id", toCustomerId);
        params.put("from_customer_id", Utility.customer.getId());
        params.put("budget_id", String.valueOf(Utility.currentBudgetId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/share-budget", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("done")) {
                        Toast.makeText(getApplicationContext(), "You shared your budget with this person", Toast.LENGTH_LONG).show();
                    } else if (obj.getString("message").equals("duplicate")) {
                        Toast.makeText(getApplicationContext(), "This budget is already shared", Toast.LENGTH_LONG).show();
                    } else if (obj.getString("message").equals("invalid")) {
                        Toast.makeText(getApplicationContext(), "Invalid customer id", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Cannot share at this moment", Toast.LENGTH_LONG).show();
                    System.out.println("champa");
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

    private void addItem() {
        Intent i = new Intent(SingleBudgetActivity.this, AddBudgetItemActivity.class);
        startActivity(i);
    }

    private void initializeData() {

        budget = Utility.budgets.get(Utility.currentBudgetId);

        if(budget!=null){

            tvBudgetName.setText(budget.getName());
            tvBudgetAmount.setText(Utility.currency + " " + String.valueOf((int)budget.getMaxAmount()));
            tvBudgetDurationValue.setText(budget.getDuration());

            loadBudgetItems();
        }
    }

    private void loadBudgetItems() {

        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        String yearMonth = year + "," + month;

        RequestParams params = new RequestParams();
        params.put("budget_id", String.valueOf(Utility.currentBudgetId));
        params.put("yearMonth", yearMonth);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-items-filtered", params, new AsyncHttpResponseHandler() {
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

                            String createDate;
                            try {
                                createDate = new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-mm-dd").parse(budgetJSON.getString("entry_date")));
                            }
                            catch(Exception ex){
                                createDate = budgetJSON.getString("entry_date");
                            }
                            budgetItem.setCreatedAt(createDate);

                            JSONObject customerJSON = budgetJSON.getJSONObject("customer");

                            budgetItem.setPersonName(customerJSON.getString("name"));

                            currentAmount += budgetJSON.getDouble("price");

                            budgetItems.add(budgetItem);
                        }

                        adapter.notifyDataSetChanged();

                        if(budgetItems.isEmpty()){
                            tvBudgetItems.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                            imgHistory.setVisibility(View.GONE);
                        }
                        else {
                            tvBudgetItems.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                        }

                        tvCurrentAmount.setText(Utility.currency + " " + String.valueOf((int)currentAmount));

                        int maxAmount = (int)budget.getMaxAmount();

                        if(currentAmount <= maxAmount)
                            tvCurrentAmount.setTextColor(Color.rgb(0,200,0));
                        else
                            tvCurrentAmount.setTextColor(Color.RED);

                    } else if (obj.getString("message").equals("empty")) {
                        tvBudgetItems.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        imgHistory.setVisibility(View.GONE);
                        tvCurrentAmount.setText(Utility.currency + " 0");
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
