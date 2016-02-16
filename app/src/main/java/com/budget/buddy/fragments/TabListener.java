package com.budget.buddy.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.budget.buddy.R;
import com.budget.buddy.data.Utility;

/**
 * Created by Ashutosh on 1/30/2016.
 */
public class TabListener implements ActionBar.TabListener {

    Fragment fragment;

    public TabListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Utility.lastTab = tab.getPosition();

        ft.replace(R.id.fragment_container, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}