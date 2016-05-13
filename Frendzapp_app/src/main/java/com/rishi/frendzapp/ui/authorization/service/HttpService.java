package com.rishi.frendzapp.ui.authorization.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rishi.frendzapp.App;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.authorization.app.Config;
import com.rishi.frendzapp.ui.authorization.app.MyApplication;
import com.rishi.frendzapp.ui.authorization.helper.PrefManager;
import com.rishi.frendzapp.ui.authorization.intializing.IntializingActivity;
import com.rishi.frendzapp.ui.verification.OtpVerificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class HttpService extends IntentService {
    private PopupWindow pwindo;
    private WindowManager windowManager;
    private static String TAG = HttpService.class.getSimpleName();
    private Handler handler;

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            //verifyOtp(otp);
            OtpVerificationActivity.setEdtOtp(otp);

        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param otp otp received in the SMS
     */
/*    private void verifyOtp(final String otp) {
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
                        //relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                        //initiatePopupWindow();
//                        Intent intent = new Intent(HttpService.this, OtpVerificationActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("otp", otp);
//                        startActivity(intent);

                        OtpVerificationActivity.edtOtp.setText(otp);
                        //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();

                    } else {
                      //  relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                   // relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Error1: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
                Log.e(TAG, "Error2: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                PrefManager pref = new PrefManager(getApplicationContext());
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


    private void initiatePopupWindow() {
        TextView txtOk;
        try {
// We need to get the instance of the LayoutInflater
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
                    Intent intent = new Intent(HttpService.this, IntializingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCustomPopupMenu()
    {
        TextView txtOk;
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.view_alert_verifed, null);
        WindowManager.LayoutParams params=new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity=Gravity.CENTER|Gravity.CENTER;
        params.x=0;
        params.y=0;
        windowManager.addView(view, params);
        txtOk = (TextView) view.findViewById(R.id.txt_ok);
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwindo.dismiss();
                Intent intent = new Intent(HttpService.this, IntializingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
*/

}
