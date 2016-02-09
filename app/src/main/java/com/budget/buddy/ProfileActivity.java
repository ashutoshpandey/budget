package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Customer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import buddy.budget.com.budgetbuddy.R;

public class ProfileActivity extends Activity {

    private TextView tvCustomerId;
    private EditText etProfileName;
    private EditText etProfilePhone;

    private Button btnUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvCustomerId = (TextView)findViewById(R.id.tvCustomerId);

        etProfileName = (EditText)findViewById(R.id.etProfileName);
        etProfilePhone = (EditText)findViewById(R.id.etProfilePhone);

        btnUpdateProfile = (Button)findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        tvCustomerId.setText("Customer Id : " + Utility.customerId);
        etProfileName.setText(Utility.customer.getName());
        etProfilePhone.setText(Utility.customer.getPhone());
    }

    private void updateProfile() {

        final String name = etProfileName.getText().toString();
        final String phone = etProfilePhone.getText().toString();

        RequestParams params = new RequestParams();
        params.put("customerId", String.valueOf(Utility.customerId));
        params.put("name", name);
        params.put("phone", phone);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utility.server + "/update-customer", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            public void onSuccess(String response) {
                System.out.println(response);
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true

                    if (obj.getString("message").equals("done")) {

                        Utility.customer.setName(name);
                        Utility.customer.setPhone(phone);

                        Toast.makeText(getApplicationContext(), "Your profile is updated", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                    }
                }catch(JSONException e){
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'

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
