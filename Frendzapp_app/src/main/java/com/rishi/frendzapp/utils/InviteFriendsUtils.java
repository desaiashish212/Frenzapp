package com.rishi.frendzapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rishi.frendzapp.App;
import com.rishi.frendzapp.ui.authorization.app.Config;
import com.rishi.frendzapp.ui.verification.OtpVerificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AD on 27-Apr-16.
 */
public class InviteFriendsUtils {
    private String mobile;
    private int userId;
    private Context context;

    public InviteFriendsUtils(Context context,String mobile,int userId){
        this.context = context;
        this.mobile = mobile;
        this.userId = userId;
        inviteFriends();
    }
    public void inviteFriends() {
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_INVITE_FRIENDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("InviteFriendsUtils", response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                    }

                    // hiding the progress bar

                } catch (JSONException e) {
                    Toast.makeText(context,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("InviteFriendsUtils", "Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            /**
             * Passing user parameters to our server
             *
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("userId", String.valueOf(userId));
                Log.e("InviteFriendsUtils", "Posting params: " + params.toString());

                return params;
            }

        };

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);
        // Adding request to request queue
        App.getInstance().addToRequestQueue(strReq);
    }
}
