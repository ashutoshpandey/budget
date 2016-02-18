package com.budget.buddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.budget.buddy.adapter.CategorySpinnerAdapter;
import com.budget.buddy.adapter.PaymentModeSpinnerAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.BudgetShare;
import com.budget.buddy.pojo.Category;
import com.budget.buddy.pojo.PaymentMode;
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

public class AddBudgetItemActivity extends Activity {

    private EditText etName;
    private EditText etPrice;
    private EditText etDate;

    private Button btnAddItem;

    private DialogFragment dateFragment;

    private Calendar mCalendar;

    private Spinner spinner;
    private Spinner spinnerPaymentModes;

    private ArrayList<Category> listCategories = new ArrayList<>();

    private ArrayList<PaymentMode> listPaymentModes = new ArrayList<>();

    public static Map<Integer,Category> categories = new HashMap<Integer,Category>();

    private CategorySpinnerAdapter adapter;
    private PaymentModeSpinnerAdapter adapterPaymentMode;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_item);

        etName = (EditText)findViewById(R.id.etItemName);
        etPrice = (EditText)findViewById(R.id.etPrice);
        etDate = (EditText)findViewById(R.id.etAddItemDate);

        btnAddItem = (Button)findViewById(R.id.btnAddItem);

        spinner = (Spinner)findViewById(R.id.spinCategories);
        spinnerPaymentModes = (Spinner)findViewById(R.id.spinPaymentModes);

        adapter = new CategorySpinnerAdapter(this, listCategories);
        adapterPaymentMode = new PaymentModeSpinnerAdapter(this, listPaymentModes);

        // Assign adapter to ListView
        spinner.setAdapter(adapter);
        spinnerPaymentModes.setAdapter(adapterPaymentMode);

        Utility.loadCategories();

        loadPaymentModes(Utility.customerId);

        initializeEvents();
    }

    private void initializeEvents() {

        if(Utility.currentBudgetType.equals("created"))
            loadCategories(Utility.budgets.get(Utility.currentBudgetId).getCustomerId());
        else
            loadCategories(Utility.budgetShares.get(Utility.currentSharedBudgetId).getBudget().getCustomerId());

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                etDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    View view = AddBudgetItemActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    new DatePickerDialog(AddBudgetItemActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddBudgetItemActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void addItem() {

        RequestParams params = new RequestParams();

        String name = etName.getText().toString();
        String price = etPrice.getText().toString();
        String date = etDate.getText().toString();
        String paymentMode = listPaymentModes.get(spinnerPaymentModes.getSelectedItemPosition()).getName();

        int categoryId = listPaymentModes.get(spinner.getSelectedItemPosition()).getId();

        if(Utility.currentBudgetType.equals("created"))
            params.put("budget_id", String.valueOf(Utility.currentBudgetId));
        else
            params.put("budget_id", String.valueOf(Utility.budgetShares.get(Utility.currentSharedBudgetId).getBudget().getId()));

        params.put("customer_id", Utility.customer.getId());
        params.put("category_id", String.valueOf(categoryId));
        params.put("name", name);
        params.put("price", price);
        params.put("date", date);
        params.put("payment_mode", paymentMode);

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

    public void loadCategories(int customerId) {

        RequestParams params = new RequestParams();
        params.put("customer_id", String.valueOf(customerId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-categories", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                categories.clear();

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

                    listCategories.clear();

                    for (Map.Entry<Integer, Category> entry : categories.entrySet())
                        listCategories.add(entry.getValue());

                    adapter.notifyDataSetChanged();

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

    public void loadPaymentModes(String customerId) {

        RequestParams params = new RequestParams();
        params.put("customer_id", customerId);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-payment-modes", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray paymentModesArray = obj.getJSONArray("paymentModes");

                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        listPaymentModes.add(paymentMode);

                        for (int i = 0; i < paymentModesArray.length(); i++) {
                            JSONObject paymentModeJSON = paymentModesArray.getJSONObject(i);

                            paymentMode = new PaymentMode();
                            paymentMode.setId(paymentModeJSON.getInt("id"));
                            paymentMode.setName(paymentModeJSON.getString("name"));

                            listPaymentModes.add(paymentMode);
                        }

                    } else if (obj.getString("message").equals("empty")) {
                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        listPaymentModes.add(paymentMode);
                    }

                    adapterPaymentMode.notifyDataSetChanged();

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
