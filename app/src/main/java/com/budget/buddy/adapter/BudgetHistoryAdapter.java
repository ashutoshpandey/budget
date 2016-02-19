package com.budget.buddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.BudgetHistoryActivity;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Budget;
import com.budget.buddy.pojo.BudgetHistory;

import java.util.ArrayList;

import com.budget.buddy.R;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class BudgetHistoryAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<BudgetHistory> budgets;
    private static LayoutInflater inflater=null;
    public Resources res;

    public BudgetHistoryAdapter(Activity activity, ArrayList<BudgetHistory> budgets){
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
        rowView = inflater.inflate(R.layout.budget_history, null);

        holder.text=(TextView) rowView.findViewById(R.id.tvBudgetListName);
        holder.amount=(TextView) rowView.findViewById(R.id.tvBudgetListAmount);

        holder.text.setText(budgets.get(position).getText());
        holder.amount.setText(Utility.currency + " " + budgets.get(position).getAmount());

        rowView.setTag(budgets.get(position).getYearMonth());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yearMonth = v.getTag().toString();
                ((BudgetHistoryActivity) activity).openHistory(yearMonth);
            }
        });
        return rowView;
    }

    public static class ViewHolder{
        public TextView text;
        public TextView amount;
    }
}
