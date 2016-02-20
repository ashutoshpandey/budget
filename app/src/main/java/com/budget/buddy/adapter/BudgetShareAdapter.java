package com.budget.buddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.HomeActivity;
import com.budget.buddy.R;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.BudgetShare;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class BudgetShareAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<BudgetShare> budgetShares;
    private static LayoutInflater inflater=null;
    public Resources res;

    public BudgetShareAdapter(Activity activity, ArrayList<BudgetShare> budgetShares){
        this.activity = activity;
        this.budgetShares = budgetShares;
    }

    @Override
    public int getCount() {
        return budgetShares.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder=new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.budget_shared_list, null);

        holder.name=(TextView) rowView.findViewById(R.id.tvBudgetListName);
        holder.sharedWith=(TextView) rowView.findViewById(R.id.tvBudgetListSharedWith);
        holder.type=(TextView) rowView.findViewById(R.id.tvBudgetListType);

        BudgetShare budgetShare = budgetShares.get(position);

        String text = budgetShare.getText();

        if(text.equals("no shares")) {
            holder.name.setText("No budgets shared");
        }
        else{
            String name = budgetShare.getBudget().getName().toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            String budgetType = budgetShare.getBudget().getBudgetType();
            budgetType = budgetType.substring(0, 1).toUpperCase() + budgetType.substring(1);

            holder.name.setText(name);
            holder.type.setText(budgetType);
            holder.sharedWith.setText(budgetShare.getCustomer().getName());

            rowView.setTag(budgetShare.getId());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int rowTag = Integer.parseInt(v.getTag().toString());
                    if(rowTag==-1)
                        return;

                    Utility.currentBudgetType = "shared";
                    Utility.currentSharedBudgetId = rowTag;

                    ((HomeActivity) activity).openSingleBudget();
                }
            });
        }
        return rowView;
    }

    public static class ViewHolder{
        public TextView name;
        public TextView sharedWith;
        public TextView type;
    }
}
