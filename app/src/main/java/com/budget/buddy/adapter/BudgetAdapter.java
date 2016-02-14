package com.budget.buddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.MainActivity;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;

import java.util.ArrayList;

import com.budget.buddy.R;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class BudgetAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<Budget> budgets;
    private static LayoutInflater inflater=null;
    public Resources res;

    public BudgetAdapter(Activity activity, ArrayList<Budget> budgets){
        this.activity = activity;
        this.budgets = budgets;
    }

    @Override
    public int getCount() {
        return budgets.size();
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
        rowView = inflater.inflate(R.layout.budget_list, null);

        holder.name=(TextView) rowView.findViewById(R.id.tvBudgetListName);
        holder.type=(TextView) rowView.findViewById(R.id.tvBudgetListType);

        Budget budget = budgets.get(position);

        String name = budget.getName().toLowerCase();

        if(name.equals("no budgets")) {
            holder.name.setText("No budgets created");
        }
        else{
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.name.setText(name);
            holder.type.setText("Type: " + budget.getBudgetType());

            rowView.setTag(budget.getId());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int rowTag = Integer.parseInt(v.getTag().toString());
                    if(rowTag==-1)
                        return;

                    Utility.currentBudgetType = "created";
                    Utility.currentBudgetId = rowTag;
                    ((MainActivity) activity).openSingleBudget();
                }
            });
        }
        return rowView;
    }

    public static class ViewHolder{
        public TextView name;
        public TextView type;
    }
}
