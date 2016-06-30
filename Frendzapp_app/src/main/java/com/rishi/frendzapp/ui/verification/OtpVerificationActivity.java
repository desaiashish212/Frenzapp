package com.rishi.frendzapp.ui.verification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rishi.frendzapp.App;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.authorization.app.Config;
import com.rishi.frendzapp.ui.authorization.helper.PrefManager;
import com.rishi.frendzapp.ui.authorization.intializing.IntializingActivity;
import com.rishi.frendzapp.ui.authorization.service.HttpService;
import com.rishi.frendzapp.ui.profile.ProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by AD on 19-Mar-16.=tttttttttttttttt
 */
public class OtpVerificationActivity extends Activity {
    private TextView txtMobileNo;
    private TextView txtVerify;
    public static EditText edtOtp;
    private PrefManager pref;
    private RelativeLayout relativeLayoutProgressBar;
    private ProgressBar progressBar;
    private PopupWindow pwindo;
    private static String TAG = OtpVerificationActivity.class.getSimpleName();
    private static  OtpVerificationActivity ad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        init();
        initListner();

    }

    public void init(){
        txtMobileNo = (TextView) findViewById(R.id.txt_mobile_no);
        edtOtp = (EditText) findViewById(R.id.edt_otp);
        txtVerify = (TextView) findViewById(R.id.txt_verify);
        relativeLayoutProgressBar = (RelativeLayout) findViewById(R.id.relativeLayoutProgressBar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pref = new PrefManager(this);
        txtMobileNo.setText(pref.getMobileNumber());
        ad = OtpVerificationActivity.this;
    }

    public void initListner(){
        txtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }

    public static void setEdtOtp(final String otp){


        ad.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               edtOtp.setText(otp);
           }
       });
    }

    private void verifyOtp() {
        String otp = edtOtp.getText().toString().trim();

        if (!otp.isEmpty()) {
            relativeLayoutProgressBar.setVisibility(View.VISIBLE);
//            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
//            grapprIntent.putExtra("otp", otp);
//            startService(grapprIntent);
            verifyOtp(otp);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }


    private void verifyOtp(final String otp) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {

                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        // parsing the user profile information
                        JSONObject profileObj = responseObj.getJSONObject("profile");

                        // String name = profileObj.getString("name");
                        String password = profileObj.getString("password");
                        String mobile = profileObj.getString("mobile");
                        String status = profileObj.getString("status");
                        String user_status = profileObj.getString("user_status");

                        PrefManager pref = new PrefManager(getApplicationContext());
                        pref.createLogin(mobile, password, status, user_status);
                        relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                        initiatePopupWindow();

                        //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();

                    } else {
                        relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Error1: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG,e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                Log.e(TAG, "Error2: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("mobile_number", pref.getMobileNumber());

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

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
                    Intent intent = new Intent(OtpVerificationActivity.this, IntializingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
