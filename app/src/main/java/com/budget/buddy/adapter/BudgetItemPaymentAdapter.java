package com.budget.buddy.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.R;
import com.budget.buddy.SingleBudgetActivity;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.BudgetItem;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class BudgetItemPaymentAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<BudgetItem> budgetItems;
    private static LayoutInflater inflater=null;
    public Resources res;

    public BudgetItemPaymentAdapter(Activity activity, ArrayList<BudgetItem> budgetItems){
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

        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder=new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.budget_item, null);

        holder.itemName=(TextView) rowView.findViewById(R.id.tvBudgetItemName);
        holder.itemPriceDate=(TextView) rowView.findViewById(R.id.tvBudgetItemPriceDate);
        holder.personName=(TextView) rowView.findViewById(R.id.tvBudgetPersonName);

        BudgetItem budgetItem = budgetItems.get(position);

        String name = budgetItem.getName();

        if(budgetItem.getId()==0){
            holder.itemName.setText(name);
            holder.itemName.setTextColor(Color.BLUE);
            holder.itemPriceDate.setVisibility(View.GONE);
            holder.personName.setVisibility(View.GONE);
        }
        else {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.itemName.setText(name);
            holder.itemPriceDate.setText(Utility.currency + " " + String.valueOf((int) budgetItems.get(position).getPrice()) + " On " + budgetItems.get(position).getCreatedAt());
            holder.personName.setText("By: " + budgetItems.get(position).getPersonName());

            rowView.setTag(position);
        }

        return rowView;
    }

    public static class ViewHolder{

        public TextView itemName;
        public TextView itemPriceDate;
        public TextView personName;
    }
}
