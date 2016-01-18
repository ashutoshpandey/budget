package com.budget.buddy;

import android.app.Activity;
import android.os.Bundle;
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

public class WelcomeActivity extends Activity {

    private Button btnCreate;

    private EditText etName;
    private EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnCreate = (Button)findViewById(R.id.btnWelcomeCreate);

        etName = (EditText)findViewById(R.id.etWelcomeName);
        etPhone = (EditText)findViewById(R.id.etWelcomePhone);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {

        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();

        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("phone", phone);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utility.getData("site_url") + "/create-customer", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            public void onSuccess(String response) {
                try {
                    System.out.println("Got response from server");
                    System.out.println(response);
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
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
