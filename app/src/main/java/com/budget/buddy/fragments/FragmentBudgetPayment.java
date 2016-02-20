package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.budget.buddy.R;
import com.budget.buddy.adapter.BudgetItemCategorizedAdapter;
import com.budget.buddy.adapter.BudgetItemPaymentAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.AmountItemList;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.pojo.PaymentMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudgetPayment extends Fragment{

    private ListView listView;
    private BudgetItemPaymentAdapter adapter;

    private ArrayList<BudgetItem> budgetItems = new ArrayList<>();
    private ArrayList<BudgetItem> budgetItemsToShow = new ArrayList<>();

    public static Map<Integer,PaymentMode> paymentModes = new HashMap<Integer,PaymentMode>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budgets_categorized, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewBudgetsCategorized);

        adapter = new BudgetItemPaymentAdapter(getActivity(), budgetItemsToShow);

        listView.setAdapter(adapter);

        return rootView;
    }

    public void setBudgetItems(ArrayList<BudgetItem> budgetItemsPassed){

        budgetItems.clear();

        for(BudgetItem budgetItem : budgetItemsPassed)
            budgetItems.add(budgetItem);

        int customerId;
        if(Utility.currentBudgetType.equals("shared"))
            customerId = Utility.getCurrentBudget().getCustomerId();
        else
            customerId = Integer.parseInt(Utility.customerId);

        loadPaymentModes(customerId);
    }

    public void groupBudgetItemsByPayments() {

        budgetItemsToShow.clear();

        // get budget items mapped to category names
        TreeMap<String, AmountItemList> map = new TreeMap<>();

        for (Map.Entry<Integer, PaymentMode> entry : paymentModes.entrySet()) {

            String name = entry.getValue().getName();

            //    ArrayList<BudgetItem> budgetItemsCategorized = getBudgetItems(name);
            AmountItemList amountItems = getAmountItems(name);

            if (amountItems != null && !amountItems.getItems().isEmpty())
                map.put(name, amountItems);
        }

        //ArrayList<BudgetItem> budgetItemsCategorized = getBudgetItems("uncategorized");
        AmountItemList amountItems = getAmountItems("uncategorized");
        if (amountItems != null && !amountItems.getItems().isEmpty())
            map.put("uncategorized",amountItems);

        for (Map.Entry<String, AmountItemList> entry : map.entrySet()) {

            String categoryName = entry.getKey();
            String modifiedPaymentModeName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);

            // represents name of category
            BudgetItem item = new BudgetItem();
            item.setId(0);
            item.setName("PaymentMode : " + modifiedPaymentModeName + " [ " + Utility.currency + " " + entry.getValue().getAmount() + " ]");

            budgetItemsToShow.add(item);

            for (BudgetItem budgetItem : entry.getValue().getItems())
                budgetItemsToShow.add(budgetItem);
        }

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private AmountItemList getAmountItems(String name) {

        ArrayList<BudgetItem> budgetItemsInPaymentMode = new ArrayList<>();
        double amount = 0;

        for(BudgetItem item : budgetItems) {

            if(item.getPaymentMode()!=null)
                if (item.getPaymentMode().toLowerCase().equals(name.toLowerCase())) {
                    budgetItemsInPaymentMode.add(item);

                    amount += item.getPrice();
                }
        }

        return new AmountItemList(budgetItemsInPaymentMode,amount);
    }

    public void loadPaymentModes(int customerId) {

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

                        paymentModes.clear();

                        JSONArray budgetSharesArray = obj.getJSONArray("paymentModes");

                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        paymentModes.put(paymentMode.getId(), paymentMode);

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            String name = budgetJSON.getString("name");
                            if(name.trim().length()==0)
                                continue;

                            paymentMode = new PaymentMode();
                            paymentMode.setId(budgetJSON.getInt("id"));
                            paymentMode.setName(name);

                            paymentModes.put(paymentMode.getId(), paymentMode);
                        }

                    } else if (obj.getString("message").equals("empty")) {
                        PaymentMode paymentMode = new PaymentMode();

                        paymentMode.setId(-1);
                        paymentMode.setName("Cash");

                        paymentModes.put(paymentMode.getId(), paymentMode);
                    }

                    groupBudgetItemsByPayments();

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
}
