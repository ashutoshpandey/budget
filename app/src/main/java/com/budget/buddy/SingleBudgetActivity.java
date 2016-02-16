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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Category;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.budget.buddy.R;

public class SingleBudgetActivity extends Activity {

    private TextView tvBudgetAmount;
    private TextView tvCurrentAmount;
    private TextView tvBudgetName;
    private TextView tvBudgetDurationValue;
    private TextView tvBudgetItems;
    private TextView tvCategorize;
    private ImageView imgHistory;

    private ListView listView;

    private Budget budget;

    private Timer timer;
    private BudgetItemsTimerTask timerTask;

    private BudgetItemAdapter adapter;
    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();
    private ArrayList<BudgetItem> budgetItemsToShow = new ArrayList<>();

    public static Map<Integer,Category> categories = new HashMap<Integer,Category>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_budget);

        tvBudgetAmount = (TextView) findViewById(R.id.tvBudgetAmount);
        tvCurrentAmount = (TextView) findViewById(R.id.tvCurrentAmount);
        tvBudgetName = (TextView) findViewById(R.id.tvBudgetName);
        tvCategorize = (TextView) findViewById(R.id.tvBudgetCategorize);
        tvBudgetItems = (TextView) findViewById(R.id.tvBudgetItems);
        tvBudgetDurationValue = (TextView) findViewById(R.id.tvBudgetDurationValue);
        imgHistory = (ImageView) findViewById(R.id.imgHistory);

        listView = (ListView) findViewById(R.id.listView);

        adapter = new BudgetItemAdapter(this, budgetItemsToShow);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BudgetHistoryActivity.class);
                startActivity(i);
            }
        });

        tvCategorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvCategorize.getText().toString().equals("Categorize")) {
                    categorizeBudgetItems(true);
                    tvCategorize.setText("Simplify");
                }
                else{
                    categorizeBudgetItems(false);
                    tvCategorize.setText("Categorize");
                }
            }
        });
    }

    private void categorizeBudgetItems(boolean categorize) {

        budgetItemsToShow.clear();

        if(categorize) {

            // get budget items mapped to category names
            TreeMap<String, ArrayList<BudgetItem>> map = new TreeMap<>();

            for (Map.Entry<Integer, Category> entry : categories.entrySet()) {

                String name = entry.getValue().getName();
System.out.println("category name = " + name);
                ArrayList<BudgetItem> budgetItems = getBudgetItems(name);

                if (budgetItems != null && !budgetItems.isEmpty())
                    map.put(name, budgetItems);
            }

            ArrayList<BudgetItem> budgetItems = getBudgetItems("uncategorized");
            if (budgetItems != null && !budgetItems.isEmpty())
                map.put("uncategorized", budgetItems);

            for (Map.Entry<String, ArrayList<BudgetItem>> entry : map.entrySet()) {
                BudgetItem item = new BudgetItem();

                String categoryName =  entry.getKey();
                categoryName = categoryName.substring(0,1).toUpperCase() + categoryName.substring(1);

                item.setId(0);      // represents name of category
                item.setName("Category : " + categoryName);

                budgetItemsToShow.add(item);

                for (BudgetItem budgetItem : entry.getValue()) {
                    budgetItemsToShow.add(budgetItem);
                }
            }
        }
        else{
            for(BudgetItem budgetItem : budgetItems)
                budgetItemsToShow.add(budgetItem);
        }

        adapter.notifyDataSetChanged();
    }

    private ArrayList<BudgetItem> getBudgetItems(String name) {

        ArrayList<BudgetItem> budgetItemsInCategory = new ArrayList<>();

        for(BudgetItem item : budgetItems) {

            if(item.getCategoryName()!=null)
                if (item.getCategoryName().toLowerCase().equals(name.toLowerCase()))
                    budgetItemsInCategory.add(item);
        }

        return budgetItemsInCategory;
    }

    @Override
    public void onResume() {
        super.onResume();

        initializeData();

        startTimer();
    }

    @Override
    public void onStop() {
        super.onResume();

        if(timer!=null){
            timer.cancel();
            timer = null;
            timerTask = null;
        }
    }

    public void startTimer(){
        timer = new Timer();
        timerTask = new BudgetItemsTimerTask();

        timer.schedule(timerTask, 60000, 60000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_budget, menu);

        if(Utility.currentBudgetType.equals("shared"))
            menu.removeItem(R.id.menu_edit_budget);

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

        input.setInputType(InputType.TYPE_CLASS_TEXT);
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

        categories.clear();

        if(Utility.currentBudgetType.equals("created")) {
System.out.println("this person created budget");
            // copy current person categories to this activity categories
            for(Map.Entry<Integer, Category> entry : Utility.categories.entrySet())
                categories.put(entry.getKey(), entry.getValue());

            budget = Utility.budgets.get(Utility.currentBudgetId);

            if(budget!=null){

                tvBudgetName.setText(budget.getName().toUpperCase());
                tvBudgetAmount.setText(Utility.currency + " " + String.valueOf((int)budget.getMaxAmount()));
                tvBudgetDurationValue.setText(budget.getDuration());

                loadBudgetItems();
            }
        }
        else {
            System.out.println("this budget is shared");
            // load categories of shared person
            BudgetShare budgetShare = Utility.budgetShares.get(Utility.currentSharedBudgetId);
            budget = budgetShare.getBudget();

            loadCategories(budget);
        }
    }

    private void loadBudgetItems() {

        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        final String yearMonth = year + "," + month;

        final RequestParams params = new RequestParams();

        final String budgetId;
        if(Utility.currentBudgetType.equals("created"))
            budgetId = String.valueOf(Utility.currentBudgetId);
        else
            budgetId = String.valueOf(Utility.budgetShares.get(Utility.currentSharedBudgetId).getBudget().getId());

        params.put("budget_id", budgetId);
        params.put("yearMonth", yearMonth);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-budget-items-filtered", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
System.out.println(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("budgetItems");

                        double currentAmount = 0;

                        budgetItems.clear();
                        budgetItemsToShow.clear();

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            BudgetItem budgetItem = new BudgetItem();

                            budgetItem.setId(budgetJSON.getInt("id"));
                            budgetItem.setName(budgetJSON.getString("name"));
                            budgetItem.setPrice(budgetJSON.getDouble("price"));

                            String createDate;
                            try {
                                createDate = new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(budgetJSON.getString("entry_date")));
                            }
                            catch(Exception ex){
                                createDate = budgetJSON.getString("entry_date");
                            }
                            budgetItem.setCreatedAt(createDate);

                            JSONObject customerJSON = budgetJSON.getJSONObject("customer");
                            budgetItem.setPersonName(customerJSON.getString("name"));

                            String categoryId = budgetJSON.isNull("category_id") ? null : budgetJSON.getString("category_id");

                            if(categoryId!=null) {
                                if(categories.containsKey(Integer.parseInt(categoryId)))
                                    budgetItem.setCategoryName(categories.get(Integer.parseInt(categoryId)).getName());
                            }
                            else
                                budgetItem.setCategoryName("Uncategorized");

                            currentAmount += budgetJSON.getDouble("price");

                            budgetItems.add(budgetItem);
                        }

                        budgetItemsToShow.clear();

                        for(BudgetItem budgetItem : budgetItems)
                            budgetItemsToShow.add(budgetItem);

                        adapter.notifyDataSetChanged();

                        if(budgetItemsToShow.isEmpty()){
                            tvBudgetItems.setVisibility(View.GONE);
                            tvCategorize.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                            imgHistory.setVisibility(View.GONE);
                        }
                        else {
                            tvBudgetItems.setVisibility(View.VISIBLE);
                            tvCategorize.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            imgHistory.setVisibility(View.VISIBLE);
                        }

                        tvCurrentAmount.setText(Utility.currency + " " + String.valueOf((int)currentAmount));

                        int maxAmount = (int)budget.getMaxAmount();

                        if(currentAmount <= maxAmount)
                            tvCurrentAmount.setTextColor(Color.rgb(0,200,0));
                        else
                            tvCurrentAmount.setTextColor(Color.RED);

                    } else if (obj.getString("message").equals("empty")) {
                        tvBudgetItems.setVisibility(View.GONE);
                        tvCategorize.setVisibility(View.GONE);
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

    public void removeBudgetItem(int id) {
        final RequestParams params = new RequestParams();

        params.put("item_id", String.valueOf(id));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/remove-item", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("removed")) {
                        loadBudgetItems();
                    }
                    else if (obj.getString("message").equals("invalid")) {
                        Toast.makeText(getApplicationContext(), "Cannot remove item", Toast.LENGTH_LONG).show();
                    }
                    else {
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

    class BudgetItemsTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    loadBudgetItems();
                }});
        }
    }

    public void loadCategories(final Budget budget) {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(budget.getCustomerId()));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-categories", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                HashMap<Integer, BudgetShare> budgetShares = new HashMap<Integer, BudgetShare>();

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("categories");

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            Category category = new Category();
                            category.setId(budgetJSON.getInt("id"));
                            category.setName(budgetJSON.getString("name"));

                            categories.put(category.getId(), category);
                        }

                    } else if (obj.getString("message").equals("empty")) {
                        Category category = new Category();

                        category.setId(-1);
                        category.setName("no categories");

                        categories.put(category.getId(), category);
                    }

                    if(budget!=null){

                        tvBudgetName.setText(budget.getName().toUpperCase());
                        tvBudgetAmount.setText(Utility.currency + " " + String.valueOf((int)budget.getMaxAmount()));
                        tvBudgetDurationValue.setText(budget.getDuration());

                        loadBudgetItems();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                System.out.println("Share status = " + statusCode);
                if (statusCode == 404) {
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                }
                // When Http response code other than 404, 500
                else {
                }
            }
        });
    }
}
