package com.budget.buddy.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.R;
import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentProfile extends Fragment{

    private TextView tvCustomerId;
    private EditText etProfileName;
    private EditText etProfilePhone;

    private Button btnUpdateProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        tvCustomerId = (TextView)rootView.findViewById(R.id.tvCustomerId);

        etProfileName = (EditText)rootView.findViewById(R.id.etProfileName);
        etProfilePhone = (EditText)rootView.findViewById(R.id.etProfilePhone);

        btnUpdateProfile = (Button)rootView.findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        tvCustomerId.setText("Customer Id : " + Utility.customerId);
        etProfileName.setText(Utility.customer.getName());
        etProfilePhone.setText(Utility.customer.getPhone());

        return rootView;
    }
    private void updateProfile() {

        final String name = etProfileName.getText().toString();
        final String phone = etProfilePhone.getText().toString();

        if(name.trim().length()==0){
            Toast.makeText(getActivity(), "Please provide name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(phone.trim().length()==0 || phone.length()!=10){
            Toast.makeText(getActivity(), "Please provide valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

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

                        Toast.makeText(getActivity(), "Your profile is updated", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                    }
                }catch(JSONException e){
                    Toast.makeText(getActivity(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'

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
