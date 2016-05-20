package com.rishi.frendzapp.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp.ui.profile.ProfileActivity;
import com.rishi.frendzapp_core.models.AppSession;

/**
 * Created by Dharendra on 26-Apr-16.
 */
public class AccountPaymentInfoActivity extends BaseLogeableActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, AccountPaymentInfoActivity.class);
        context.startActivity(intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_account_payment_info);

        TextView numberTxt = (TextView) findViewById(R.id.txt_number);
        numberTxt.setText(AppSession.getSession().getUser().getLogin());
//        ActionBar actionBar=getSupportActionBar();
//        setSupportActionBar(actionBar);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

