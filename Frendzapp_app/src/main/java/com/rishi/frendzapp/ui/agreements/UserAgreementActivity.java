package com.rishi.frendzapp.ui.agreements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseActivity;

public class UserAgreementActivity extends BaseActivity {

    private WebView userAgreementWebView;

    public static void start(Context context) {
        Intent intent = new Intent(context, UserAgreementActivity.class);
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        initUserAgreementWebView();
    }

    private void initUserAgreementWebView() {
        userAgreementWebView = (WebView) findViewById(R.id.user_agreement_webview);
        userAgreementWebView.getSettings().setJavaScriptEnabled(true);
        String policyLink = getResources().getString(R.string.uag_policy_link);
        userAgreementWebView.loadUrl(policyLink);
    }
}