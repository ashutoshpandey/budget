package com.budget.buddy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.budget.buddy.adapter.BudgetItemAdapter;
import com.budget.buddy.adapter.CategoryAdapter;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.BudgetItem;
import com.budget.buddy.pojo.Category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CategoryActivity extends Activity {

    private ListView listView;

    private CategoryAdapter adapter;

    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        listView = (ListView)findViewById(R.id.lvCategories);

        adapter = new CategoryAdapter(this, categories);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }
}
