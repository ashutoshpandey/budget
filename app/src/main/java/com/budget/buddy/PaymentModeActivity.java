package com.budget.buddy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.budget.buddy.adapter.PaymentModeAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.PaymentMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PaymentModeActivity extends Activity {

    private ListView listView;

    private PaymentModeAdapter adapter;

    private ArrayList<PaymentMode> paymentModes = new ArrayList<>();

    private Button btnCreate;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        listView = (ListView)findViewById(R.id.lvPaymentModes);

        btnCreate = (Button)findViewById(R.id.btnAddPaymentMode);
        etName = (EditText)findViewById(R.id.etPaymentModeName);

        adapter = new PaymentModeAdapter(this, paymentModes);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPaymentMode();
            }
        });

        loadPaymentModes();
    }

    private void addPaymentMode() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        RequestParams params = new RequestParams();

        String name = etName.getText().toString();

        params.put("customer_id", Utility.customer.getId());
        params.put("name", name);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/add-payment-mode", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("done")) {
                        Toast.makeText(getApplicationContext(), "Payment mode added", Toast.LENGTH_LONG).show();

                        etName.setText("");

                        loadPaymentModes();
                    } else if (obj.getString("message").equals("duplicate"))
                        Toast.makeText(getApplicationContext(), "Duplicate payment mode", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadPaymentModes() {

        final RequestParams params = new RequestParams();

        params.put("customer_id", Utility.customerId);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-payment-modes", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                paymentModes.clear();

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray paymentModesArray = obj.getJSONArray("paymentModes");

                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        paymentModes.add(paymentMode);

                        for (int i = 0; i < paymentModesArray.length(); i++) {
                            JSONObject paymentModeJSON = paymentModesArray.getJSONObject(i);

                            paymentMode = new PaymentMode();

                            paymentMode.setId(paymentModeJSON.getInt("id"));
                            paymentMode.setName(paymentModeJSON.getString("name"));

                            paymentModes.add(paymentMode);
                        }

                        adapter.notifyDataSetChanged();

                    } else if (obj.getString("message").equals("empty")) {
                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        paymentModes.add(paymentMode);

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

    public void removePaymentMode(int paymentModeId) {

        RequestParams params = new RequestParams();

        params.put("id", String.valueOf(paymentModeId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/remove-payment-mode", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("done")) {
                        Toast.makeText(getApplicationContext(), "Payment mode removed", Toast.LENGTH_LONG).show();

                        loadPaymentModes();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
