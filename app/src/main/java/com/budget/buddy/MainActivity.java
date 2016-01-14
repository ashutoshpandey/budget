package com.budget.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import buddy.budget.com.budgetbuddy.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_new:
                createBudget();
                break;

            case R.id.menu_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createBudget() {
        Intent i = new Intent(MainActivity.this, NewBudgetActivity.class);
        startActivity(i);
    }

    private void logout(){
        finish();
    }
}
