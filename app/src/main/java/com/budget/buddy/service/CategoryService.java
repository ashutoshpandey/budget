package com.budget.buddy.service;

import com.budget.buddy.data.Utility;
import com.budget.buddy.pojo.Category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ashutosh on 2/19/2016.
 */
public class CategoryService {

    public void loadCategories(String customerId, final Map<Integer, Category> categories) {

        categories.clear();

        RequestParams params = new RequestParams();
        params.put("customer_id", customerId);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Utility.server + "/all-categories", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("message").equals("found")) {

                        JSONArray budgetSharesArray = obj.getJSONArray("categories");

                        for (int i = 0; i < budgetSharesArray.length(); i++) {
                            JSONObject budgetJSON = budgetSharesArray.getJSONObject(i);

                            Category category = new Category();
                            category.setId(budgetJSON.getInt("id"));
                            category.setName(budgetJSON.getString("name"));

                            categories.put(category.getId(), category);
                        }

                    } else if (obj.getString("message").equals("empty")) {
                        Category category = new Category();

                        category.setId(-1);
                        category.setName("no categories");

                        categories.put(category.getId(), category);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // When Http response code is '404'

                if (statusCode == 404) {
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                }
                // When Http response code other than 404, 500
                else {
                }
            }
        });
    }
}
