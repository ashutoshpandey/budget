package com.budget.buddy.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.SingleBudgetActivity;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.BudgetItem;

import java.util.ArrayList;

import com.budget.buddy.R;

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

        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder=new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.budget_item, null);

        holder.itemName=(TextView) rowView.findViewById(R.id.tvBudgetItemName);
        holder.itemPriceDate=(TextView) rowView.findViewById(R.id.tvBudgetItemPriceDate);
        holder.personName=(TextView) rowView.findViewById(R.id.tvBudgetPersonName);

        String name = budgetItems.get(position).getName().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        holder.itemName.setText(name);
        holder.itemPriceDate.setText(Utility.currency + " " + String.valueOf((int)budgetItems.get(position).getPrice()) + " On " + budgetItems.get(position).getCreatedAt());
        holder.personName.setText("By: " + budgetItems.get(position).getPersonName());

        rowView.setTag(position);

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int position = Integer.parseInt(view.getTag().toString());

                final BudgetItem budgetItemToRemove = budgetItems.get(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("Delete?");
                alertDialog.setMessage("Delete this item?");
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((SingleBudgetActivity)activity).removeBudgetItem(budgetItemToRemove.getId());
                    }
                });

                alertDialog.show();

                return true;
            }
        });

        return rowView;
    }

    public static class ViewHolder{

        public TextView itemName;
        public TextView itemPriceDate;
        public TextView personName;
    }
}
