package com.asg.ashish.privacybrowser.ApiCaller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asg.ashish.privacybrowser.Interfaces.InstanceAccessor;
import com.asg.ashish.privacybrowser.Utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VolleyUtil {

    private Context context;
    private InstanceAccessor accessor;

    public VolleyUtil(Context context, InstanceAccessor accessor){
        this.context = context;
        this.accessor = accessor;
    }

    public void getRequest(String query){
        String url = Constants.GOOGLE_SUGGEST + query;
        final List<String> suggestions = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response).getJSONArray(1);
                            for(int i = 0; i < array.length(); i++){
                                suggestions.add(array.getString(i));
                            }
                            accessor.setAdapter(suggestions);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Error: ", error.getMessage() + " " + error.getCause());
                    }
                });
        requestQueue.add(request);

    }

}
