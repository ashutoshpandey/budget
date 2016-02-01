package com.budget.buddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import buddy.budget.com.budgetbuddy.R;

public class AddBudgetItemActivity extends Activity {

    private EditText etName;
    private EditText etPrice;
    private EditText etDate;

    private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_item);

        etName = (EditText)findViewById(R.id.etItemName);
        etPrice = (EditText)findViewById(R.id.etPrice);
        etDate = (EditText)findViewById(R.id.etAddItemDate);

        btnAddItem = (Button)findViewById(R.id.btnAddItem);

        initializeEvents();
    }

    private void initializeEvents() {

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void addItem() {

        RequestParams params = new RequestParams();

        String name = etName.getText().toString();
        String price = etPrice.getText().toString();
        String date = etDate.getText().toString();

        params.put("customer_id", Utility.customer.getId());
        params.put("budget_id", Utility.currentBudgetId);
        params.put("name", name);
        params.put("price", price);
        params.put("date", date);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/add-budget-item", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("done")) {
                        Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_LONG).show();

                        etName.setText("");
                        etPrice.setText("");
                        etDate.setText("");

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
