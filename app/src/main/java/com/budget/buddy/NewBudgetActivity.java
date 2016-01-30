package com.budget.buddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.budget.buddy.com.budget.buddy.pojo.Customer;
import com.budget.buddy.data.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import buddy.budget.com.budgetbuddy.R;

public class NewBudgetActivity extends Activity {

    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etBudgetName;

    private RadioGroup rdGroupDate;

    private Button btnCreateBudget;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_budget);

        etBudgetName = (EditText)findViewById(R.id.etBudgetName);
        etStartDate = (EditText)findViewById(R.id.etCreateBudgetStartDate);
        etEndDate = (EditText)findViewById(R.id.etCreateBudgetEndDate);

        btnCreateBudget = (Button)findViewById(R.id.btnCreateBudget);

        rdGroupDate = (RadioGroup)findViewById(R.id.rdGroupDate);

        initializeEvents();
    }

    private void initializeEvents() {

        btnCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBudget();
            }
        });

        final DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                setStartDate();
            }

        };

        final DatePickerDialog.OnDateSetListener endStart = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                setEndDate();
            }

        };

        etStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewBudgetActivity.this, dateStart, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewBudgetActivity.this, dateStart, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        rdGroupDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdBudgetTypeMonthly) {
                    etStartDate.setVisibility(View.GONE);
                    etEndDate.setVisibility(View.GONE);
                } else if (checkedId == R.id.rdBudgetTypeDateRange) {
                    etStartDate.setVisibility(View.VISIBLE);
                    etEndDate.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void setStartDate() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etStartDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void setEndDate() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etEndDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void createBudget() {

        RequestParams params = new RequestParams();

        String name = etBudgetName.getText().toString();

        params.put("customer_id", Utility.customer.getId());
        params.put("name", name);

        if(rdGroupDate.getCheckedRadioButtonId()==R.id.rdBudgetTypeMonthly){
            params.put("budget_type", "monthly");
        }
        else{
            params.put("budget_type", "date range");

            String startDate = etStartDate.getText().toString();
            String endDate = etEndDate.getText().toString();

            params.put("start_date", startDate);
            params.put("end_date", endDate);
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/create-budget", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("done")) {
                        Toast.makeText(getApplicationContext(), "Budget created", Toast.LENGTH_LONG).show();

                        etBudgetName.setText("");
                        etStartDate.setText("");
                        etEndDate.setText("");
                        rdGroupDate.check(R.id.rdBudgetTypeMonthly);

                    } else if (obj.getString("message").equals("duplicate")) {
                        Toast.makeText(getApplicationContext(), "Duplicate budget name", Toast.LENGTH_LONG).show();
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
