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

import com.budget.buddy.HomeActivity;
import com.budget.buddy.PaymentModeActivity;
import com.budget.buddy.R;
import com.budget.buddy.fragments.FragmentPayment;
import com.budget.buddy.pojo.PaymentMode;
import com.budget.buddy.service.PaymentModeService;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class PaymentModeAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<PaymentMode> paymentModes;
    private static LayoutInflater inflater=null;
    public Resources res;

    private PaymentModeService paymentModeService;

    public PaymentModeAdapter(Activity activity, ArrayList<PaymentMode> paymentModes){
        this.activity = activity;
        this.paymentModes = paymentModes;

        paymentModeService = new PaymentModeService();
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
            holder.name.setText("No paymentModes created");
        }
        else{
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.name.setText(name);

            rowView.setTag(paymentMode.getId());

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int paymentModeId = Integer.parseInt(v.getTag().toString());
                    if (paymentModeId != -1) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setTitle("Delete?");
                        alertDialog.setMessage("Delete this item?");
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                paymentModeService.removePaymentMode(paymentModeId);
                            }
                        });

                        alertDialog.show();

                        return true;
                    }

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
