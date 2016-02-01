package com.budget.buddy.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.pojo.BudgetItem;

import java.util.ArrayList;

import buddy.budget.com.budgetbuddy.R;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class BudgetItemAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<BudgetItem> budgetItems;
    private static LayoutInflater inflater=null;
    public Resources res;

    public BudgetItemAdapter(Activity activity, ArrayList<BudgetItem> budgetItems){
        this.activity = activity;
        this.budgetItems = budgetItems;
    }

    @Override
    public int getCount() {
        return budgetItems.size();
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
        ViewHolder holder=new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.budget_item, null);

        holder.itemName=(TextView) rowView.findViewById(R.id.tvBudgetItemName);
        holder.itemPrice=(TextView) rowView.findViewById(R.id.tvBudgetItemPrice);
        holder.createDate=(TextView) rowView.findViewById(R.id.tvBudgetItemDate);

        holder.itemName.setText(budgetItems.get(position).getName());
        holder.itemPrice.setText(String.valueOf(budgetItems.get(position).getPrice()));
        holder.createDate.setText(budgetItems.get(position).getCreatedAt());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rowView;
    }

    public static class ViewHolder{

        public TextView itemName;
        public TextView itemPrice;
        public TextView createDate;
    }
}
