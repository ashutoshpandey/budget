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
import com.budget.buddy.R;
import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Category;

import java.util.ArrayList;

/**
 * Created by Ashutosh on 2/1/2016.
 */
public class CategoryAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<Category> categories;
    private static LayoutInflater inflater=null;
    public Resources res;

    public CategoryAdapter(Activity activity, ArrayList<Category> categories){
        this.activity = activity;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
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
        rowView = inflater.inflate(R.layout.category_list, null);

        holder.name=(TextView) rowView.findViewById(R.id.tvCategoryName);

        Category category = categories.get(position);

        String name = category.getName().toLowerCase();

        if(name.equals("no categories")) {
            holder.name.setText("No categories created");
        }
        else{
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            holder.name.setText(name);

            rowView.setTag(category.getId());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int rowTag = Integer.parseInt(v.getTag().toString());
                    if(rowTag==-1)
                        return;
                }
            });
        }
        return rowView;
    }

    public static class ViewHolder{
        public TextView name;
    }
}
