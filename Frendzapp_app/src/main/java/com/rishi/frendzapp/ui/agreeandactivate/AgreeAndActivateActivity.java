package com.rishi.frendzapp.ui.agreeandactivate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.agreements.UserAgreementActivity;
import com.rishi.frendzapp.ui.authorization.base.BaseAuthActivity;
import com.rishi.frendzapp.ui.verification.VerificationActivity;

/**
 * Created by AD on 19-Mar-16.
 */
public class AgreeAndActivateActivity extends BaseAuthActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, AgreeAndActivateActivity.class);
        context.startActivity(intent);
    }

    private TextView txtAgree;
    private TextView txtTerms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activtity_agree_and_activate);
        init();
        initListner();
    }
    private void init(){
        txtAgree = (TextView) findViewById(R.id.txt_agree);
        txtTerms = (TextView) findViewById(R.id.txt_terms);
    }

    private void initListner(){
        txtAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgreeAndActivateActivity.this, VerificationActivity.class));
                finish();
            }
        });

        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAgreementActivity.start(AgreeAndActivateActivity.this);
            }
        });
    }
}
