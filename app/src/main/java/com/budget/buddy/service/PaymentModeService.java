package com.budget.buddy.service;

import android.app.Fragment;

import com.budget.buddy.data.Utility;
import com.budget.buddy.fragments.FragmentPayment;
import com.budget.buddy.pojo.PaymentMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/19/2016.
 */
public class PaymentModeService {

    public void loadPaymentModes(final Fragment fragmentPayment, final ArrayList<PaymentMode> paymentModes) {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(Utility.customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-payment-modes", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        Utility.paymentModes.clear();
                        paymentModes.clear();

                        JSONArray budgetSharesArray = obj.getJSONArray("paymentModes");

                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        Utility.paymentModes.put(paymentMode.getId(), paymentMode);

                        paymentModes.add(paymentMode);

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            String name = budgetJSON.getString("name");
                            if(name.trim().length()==0)
                                continue;

                            paymentMode = new PaymentMode();
                            paymentMode.setId(budgetJSON.getInt("id"));
                            paymentMode.setName(name);

                            Utility.paymentModes.put(paymentMode.getId(), paymentMode);
                            paymentModes.add(paymentMode);
                        }

                    } else if (obj.getString("message").equals("empty")) {
                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        Utility.paymentModes.put(paymentMode.getId(), paymentMode);
                        paymentModes.add(paymentMode);
                    }

                    Utility.paymentModeCount = paymentModes.size();

                    ((FragmentPayment) fragmentPayment).refreshData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'

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
                        //Toast.makeText(getApplicationContext(), "Payment mode removed", Toast.LENGTH_LONG).show();

                        FragmentPayment.me().loadPaymentModes();

                        //loadPaymentModes(FragmentPayment.this);
                    }
                    else
                        ;//Toast.makeText(getApplicationContext(), "Invalid data", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    //Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    //Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void addPaymentMode(String name, final FragmentPayment fragmentPayment) {

        RequestParams params = new RequestParams();

        params.put("customer_id", Utility.customer.getId());
        params.put("name", name);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/add-payment-mode", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if(obj.has("message"))
                        fragmentPayment.paymentModeAdded(obj.getString("message"));
                    else
                        fragmentPayment.paymentModeAdded(null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    ;
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    ;
                }
                // When Http response code other than 404, 500
                else {
                    ;
                }
            }
        });
    }
}
