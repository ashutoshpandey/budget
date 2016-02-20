package com.budget.buddy.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.budget.buddy.R;
import com.budget.buddy.adapter.PaymentModeAdapter;
import com.budget.buddy.pojo.PaymentMode;
import com.budget.buddy.service.PaymentModeService;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentPayment extends Fragment{

    private ListView listView;

    private PaymentModeAdapter adapter;

    private PaymentModeService paymentModeService;

    private ArrayList<PaymentMode> paymentModes = new ArrayList<>();

    private Button btnCreate;
    private EditText etName;

    private static FragmentPayment selfObject;
    public static FragmentPayment me(){
        return selfObject;
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_modes, container, false);

        selfObject = this;

        listView = (ListView)rootView.findViewById(R.id.lvPaymentModes);

        btnCreate = (Button)rootView.findViewById(R.id.btnAddPaymentMode);
        etName = (EditText)rootView.findViewById(R.id.etPaymentModeName);

        adapter = new PaymentModeAdapter(getActivity(), paymentModes);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPaymentMode();
            }
        });

        paymentModeService = new PaymentModeService();

        loadPaymentModes();

        return rootView;
    }

    public void loadPaymentModes(){
        paymentModeService.loadPaymentModes(FragmentPayment.this, paymentModes);
    }

    public void refreshData(){
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

    private void addPaymentMode() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        String name = etName.getText().toString();
        if(name.trim().length()==0){
            Toast.makeText(getActivity(), "Please provide a name", Toast.LENGTH_SHORT).show();
            return;
        }

        paymentModeService.addPaymentMode(name, FragmentPayment.this);
    }

    public void paymentModeAdded(String message) {

        if (message.equals("done")) {
            Toast.makeText(getActivity(), "Payment mode added", Toast.LENGTH_LONG).show();

            etName.setText("");

            paymentModeService.loadPaymentModes(FragmentPayment.this, paymentModes);
        }
        else if (message.equals("duplicate"))
            Toast.makeText(getActivity(), "Duplicate payment mode", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();
    }
}

