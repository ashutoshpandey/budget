package com.budget.buddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.R;
import com.budget.buddy.pojo.PaymentMode;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class PaymentModeSpinnerAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<PaymentMode> paymentModes;
    private static LayoutInflater inflater=null;
    public Resources res;

    public PaymentModeSpinnerAdapter(Activity activity, ArrayList<PaymentMode> paymentModes){
        this.activity = activity;
        this.paymentModes = paymentModes;
    }

    @Override
    public int getCount() {
        return paymentModes.size();
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
        rowView = inflater.inflate(R.layout.payment_mode_list, null);

        holder.name=(TextView) rowView.findViewById(R.id.tvPaymentMode);

        PaymentMode paymentMode = paymentModes.get(position);

        String name = paymentMode.getName().toLowerCase();

        if(name.equals("no paymentModes")) {
            holder.name.setText("Uncategorized");
            rowView.setTag(0);
        }
        else{
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.name.setText(name);

            rowView.setTag(paymentMode.getId());

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int paymentModeId = Integer.parseInt(v.getTag().toString());

                    return true;
                }
            });
        }
        return rowView;
    }

    public static class ViewHolder{
        public TextView name;
    }
}
