package com.thesejongproject.weservice;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.thesejongproject.smart.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SmartWebManager implements Constants {

    private static final int TIMEOUT = 100000;

    public enum REQUEST_METHOD_PARAMS {URL, CONTEXT, PARAMS, TAG, RESPONSE_LISTENER}

    private static SmartWebManager mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private SmartWebManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SmartWebManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SmartWebManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams) {
        Log.v("@@@@@WSUrl", (String) requestParams.get(REQUEST_METHOD_PARAMS.URL));
        JSONObject jsonBody = (JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS);
        Log.v("@@@@@WSParameters", jsonBody.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, (String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("@@@@@WSResponse", response.toString());
                        if (getResponseCode(response) == 200) {

                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {

                            int responseCode = getResponseCode(response);
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.v("@@@@@Error", volleyError.toString());
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsonObjectRequest);
    }

    public int getResponseCode(JSONObject response) {
        if (response.has("Status")) {
            try {
                if (response.getBoolean("Status")) {
                    return 200;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                return 404;
            }
        }
        return 404;
    }

    public interface OnResponseReceivedListener {
        void onResponseReceived(JSONObject tableRows, boolean isValidResponse, int responseCode);

        void onResponseError();
    }
}