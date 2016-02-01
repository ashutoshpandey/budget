package com.budget.buddy.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import buddy.budget.com.budgetbuddy.R;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class FragmentDashboard extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_dashboard, container, false);
        return rootView;
    }
}
