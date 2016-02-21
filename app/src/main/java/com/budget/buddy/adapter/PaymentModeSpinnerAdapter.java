package com.budget.buddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budget.buddy.R;
import com.budget.buddy.pojo.PaymentMode;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class PaymentModeSpinnerAdapter extends ArrayAdapter<PaymentMode>{

    private Activity activity;
    private ArrayList<PaymentMode> paymentModes;
    private static LayoutInflater inflater=null;
    public Resources res;

    public PaymentModeSpinnerAdapter(Activity activity, ArrayList<PaymentMode> paymentModes){
        super(activity, R.layout.payment_mode_list, paymentModes);
        this.activity = activity;
        this.paymentModes = paymentModes;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.payment_mode_list, parent, false);

        /***** Get each Model object from Arraylist ********/
        PaymentMode paymentMode = paymentModes.get(position);

        TextView label = (TextView) row.findViewById(R.id.tvPaymentMode);

        if(position==0){

            // Default selected Spinner item
            label.setText("Cash");
        }
        else
        {
            String name = paymentMode.getName();
            name = name.substring(0,1).toUpperCase() + name.substring(1);

            label.setText(name);
        }

        return row;
    }

    public static class ViewHolder{
        public TextView name;
    }
}
