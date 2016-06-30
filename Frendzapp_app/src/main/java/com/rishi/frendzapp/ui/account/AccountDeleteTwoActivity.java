package com.rishi.frendzapp.ui.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.App;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.authorization.app.Config;
import com.rishi.frendzapp.ui.authorization.helper.PrefManager;
import com.rishi.frendzapp.ui.authorization.intializing.IntializingActivity;
import com.rishi.frendzapp.ui.splash.SplashActivity;
import com.rishi.frendzapp.ui.verification.OtpVerificationActivity;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dharendra on 21-May-16.
 */
public class AccountDeleteTwoActivity extends Activity{

String TAG = AccountDeleteTwoActivity.class.getSimpleName();
    private Button btn_Delete;
    private PopupWindow pwindo;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_two);
        init();
        setLis();
    }
    public void init()
    {
        btn_Delete=(Button)findViewById(R.id.btn_delet_account);
        progressDialog = new ProgressDialog(this);

    }
    public void setLis()
    {
        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });
    }

    private void deleteAccount(final String mobile,final String userId,final String password) {

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_DELETE_ACCOUNT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

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
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        PrefManager pref = new PrefManager(getApplicationContext());
                        pref.clearSession();
                        PrefsHelper helper = new PrefsHelper(AccountDeleteTwoActivity.this);
                        helper.delete();
                        Intent i1=new Intent(getApplicationContext(),SplashActivity.class);
                        i1.setAction(Intent.ACTION_MAIN);
                        i1.addCategory(Intent.CATEGORY_HOME);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i1);
                        finish();
                     //   pwindo.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                      //  pwindo.dismiss();
                    }

                    // hiding the progress bar
                   // progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                 //   progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
              //  progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("userid", userId);
                params.put("password", password);
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);
        // Adding request to request queue
        App.getInstance().addToRequestQueue(strReq);
    }

    public void initiatePopupWindow() {
        TextView txtOk;
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_alert_verifed, null);
            pwindo = new PopupWindow(layout, width, height, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            txtOk = (TextView) layout.findViewById(R.id.txt_ok);

            txtOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    progressDialog.show();
                    // String Phone= AppSession.getSession().getUser().getPhone();
                    QBUser user = AppSession.getSession().getUser();
                    deleteAccount(user.getLogin(),String.valueOf(user.getId()),user.getPassword());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
