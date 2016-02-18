package com.budget.buddy.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.budget.buddy.R;
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

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentPayment extends Fragment{

    private ListView listView;

    private PaymentModeAdapter adapter;

    private ArrayList<PaymentMode> paymentModes = new ArrayList<>();

    private Button btnCreate;
    private EditText etName;

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_modes, container, false);


        listView = (ListView)rootView.findViewById(R.id.lvPaymentModes);

        btnCreate = (Button)rootView.findViewById(R.id.btnAddPaymentMode);
        etName = (EditText)rootView.findViewById(R.id.etPaymentModeName);

        adapter = new PaymentModeAdapter(getActivity(), paymentModes);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPaymentMode();
            }
        });

        loadPaymentModes();
        
        return rootView;
    }

    private void addPaymentMode() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        Toast.makeText(getActivity(), "Payment mode added", Toast.LENGTH_LONG).show();

                        etName.setText("");

                        loadPaymentModes();
                    } else if (obj.getString("message").equals("duplicate"))
                        Toast.makeText(getActivity(), "Duplicate payment mode", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity(), "Cannot connect to server", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Payment mode removed", Toast.LENGTH_LONG).show();

                        loadPaymentModes();
                    } else
                        Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

