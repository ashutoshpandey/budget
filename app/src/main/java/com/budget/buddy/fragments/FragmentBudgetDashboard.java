package com.budget.buddy.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.AddBudgetItemActivity;
import com.budget.buddy.BudgetHistoryActivity;
import com.budget.buddy.R;
import com.budget.buddy.adapter.BudgetAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.service.BudgetService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentBudgetDashboard extends Fragment{

    private TextView tvBudgetAmount;
    private TextView tvCurrentAmount;
    private TextView tvBudgetName;
    private TextView tvBudgetDurationValue;

    private ImageView imgViewAddItem;
    private ImageView imgViewShareBudget;
    private ImageView imgViewHistory;
    private ImageView imgViewRemove;

    private BudgetService budgetService;

    @Override
    public void onResume(){
        super.onResume();

        refreshDashboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budget_dashboard, container, false);

        tvBudgetAmount = (TextView)rootView.findViewById(R.id.tvBudgetAmount);
        tvCurrentAmount = (TextView)rootView.findViewById(R.id.tvCurrentAmount);
        tvBudgetName = (TextView)rootView.findViewById(R.id.tvBudgetName);
        tvBudgetDurationValue = (TextView)rootView.findViewById(R.id.tvBudgetDurationValue);

        imgViewAddItem = (ImageView)rootView.findViewById(R.id.imgViewAddItem);
        imgViewShareBudget = (ImageView)rootView.findViewById(R.id.imgViewShareBudget);
        imgViewHistory = (ImageView)rootView.findViewById(R.id.imgHistory);
        imgViewRemove = (ImageView)rootView.findViewById(R.id.imgViewRemove);

        budgetService = new BudgetService();

        imgViewAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddBudgetItemActivity.class);
                startActivity(i);
            }
        });

        imgViewShareBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareBudget();
            }
        });

        imgViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BudgetHistoryActivity.class);
                startActivity(i);
            }
        });

        imgViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBudget();
            }
        });

        return rootView;
    }

    private void removeBudget() {
        
        if(Utility.currentBudgetType.equals("shared")){

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Stop sharing?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   removeBudgetShare();

                    getActivity().finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.show();            
        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Remove this budget?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeMyBudget();

                    getActivity().finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.show();            
        }
    }

    private void removeMyBudget() {
        budgetService.removeBudget(Utility.currentBudgetId);;
    }

    private void removeBudgetShare() {
        budgetService.removeBudgetShare(Utility.currentSharedBudgetId);
    }

    private void shareBudget() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter other person customer id");

        final EditText input = new EditText(getActivity());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();

                shareBudgetNow(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private void shareBudgetNow(String toCustomerId) {

        RequestParams params = new RequestParams();

        params.put("to_customer_id", toCustomerId);
        params.put("from_customer_id", Utility.customer.getId());
        params.put("budget_id", String.valueOf(Utility.currentBudgetId));

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Utility.server + "/share-budget", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("done")) {
                        Toast.makeText(getActivity(), "You shared your budget with this person", Toast.LENGTH_LONG).show();
                        budgetService.loadShares();
                    } else if (obj.getString("message").equals("duplicate")) {
                        Toast.makeText(getActivity(), "This budget is already shared", Toast.LENGTH_LONG).show();
                    } else if (obj.getString("message").equals("invalid")) {
                        Toast.makeText(getActivity(), "Invalid customer id", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Cannot share at this moment", Toast.LENGTH_LONG).show();
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

    public void refreshDashboard() {

        Budget budget = Utility.getCurrentBudget();

        String name = budget.getName();
        String duration = budget.getDuration();
        double currentAmount = Utility.currentBudgetCurrentAmount;
        double maxAmount = budget.getMaxAmount();

        name = name.substring(0,1).toUpperCase() + name.substring(1);

        tvBudgetName.setText(name);
        tvBudgetAmount.setText(Utility.currency + " " + maxAmount);
        tvBudgetDurationValue.setText(duration);

        if (currentAmount <= maxAmount)
            tvCurrentAmount.setTextColor(Color.rgb(0, 200, 0));
        else
            tvCurrentAmount.setTextColor(Color.RED);

        if (currentAmount==0)
            tvCurrentAmount.setText(Utility.currency + " 0");
        else
            tvCurrentAmount.setText(Utility.currency + " " + String.valueOf((int) currentAmount));
    }
}
