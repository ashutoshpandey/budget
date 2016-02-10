package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Customer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.budget.buddy.R;

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
        client.post(Utility.server + "/create-customer", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            public void onSuccess(String response) {
                System.out.println(response);
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true

                    if (obj.getString("message").equals("done")) {

                        //JSONArray customerArray = obj.getJSONArray("customer");
                        //JSONObject customer = customerArray.getJSONObject(0);
                        JSONObject customer = obj.getJSONObject("customer");

                        String id = customer.getString("id");
                        String name = customer.getString("name");
                        String phone = customer.getString("photo");
                        String photo = customer.getString("photo");
                        String status = customer.getString("status");
                        String createdAt = customer.getString("created_at");

                        Utility.customerId = id;
                        Utility.customer = new Customer(id, name, phone, photo, status, createdAt);

                        writeCustomerDataToFile();

                        Toast.makeText(getApplicationContext(), "You are successfully registered", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else if (obj.getString("message").equals("duplicate")) {
                        Toast.makeText(getApplicationContext(), "This phone number is already registered", Toast.LENGTH_LONG).show();
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

    private void writeCustomerDataToFile() {
        File sdcard = Environment.getExternalStorageDirectory();

        File file = new File(sdcard, "budget.dat");

        DataOutputStream dout = null;
        try {
            dout = new DataOutputStream(new FileOutputStream(file));

            dout.writeUTF(Utility.customerId);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Application needs file permission", Toast.LENGTH_LONG).show();
        } finally {
            try {
                dout.close();
            } catch (Exception ex) {
            }
        }
    }
}
