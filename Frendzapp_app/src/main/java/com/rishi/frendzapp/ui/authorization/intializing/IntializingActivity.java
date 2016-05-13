package com.rishi.frendzapp.ui.authorization.intializing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.authorization.base.BaseAuthActivity;
import com.rishi.frendzapp.ui.authorization.helper.PrefManager;
import com.rishi.frendzapp.utils.AnalyticsUtils;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.qb.commands.QBLoginCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import java.util.HashMap;

/**
 * Created by AD on 12/22/2015.
 */
public class IntializingActivity extends BaseAuthActivity {
    private String user_id;
    private String password;
    private String status;
    private PrefManager pref;
    private RelativeLayout relativeLayoutInitializing;
    private RelativeLayout relativeLayoutCountinue;
    private TextView txtContinue;
    private QBUser user;
    HashMap<String, String> profile;
    public static void start(Context context) {
        Intent intent = new Intent(context, IntializingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initializing);
        initUI();
        initListner();
        login(user_id, password);
        addActions();
    }

    private void initUI() {
        pref = new PrefManager(getApplicationContext());
        relativeLayoutInitializing = (RelativeLayout) findViewById(R.id.relativeLayoutInitializing);
        relativeLayoutCountinue = (RelativeLayout) findViewById(R.id.relativeLayoutContinue);
        txtContinue = (TextView) findViewById(R.id.txt_continue);

  /*      Bundle b = getIntent().getExtras();
        user_id = b.getString("user_id");
        password = b.getString("password");
        status = b.getString("status");  */
        profile = pref.getUserDetails();
        user_id = profile.get("mobile");
        password = profile.get("password");
        status = profile.get("status");
    }

    private void initListner(){
        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saveRememberMe = true;
                PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_REMEMBER_ME, saveRememberMe);
                pref.setIsremember(saveRememberMe);
                if (Integer.parseInt(status)==1){
                    startMainActivity(IntializingActivity.this, user, saveRememberMe);
                }else{
                    startSelectBirthday_GenderActivity(IntializingActivity.this, user, saveRememberMe);
                }
            }
        });
    }
    private void addActions() {
        addAction(QBServiceConsts.LOGIN_SUCCESS_ACTION, new LoginSuccessAction());
        addAction(QBServiceConsts.LOGIN_FAIL_ACTION, new LoginFailAction());
        updateBroadcastActionList();
    }

    private void login(String userLogin, String userPassword) {
        System.out.println("userLogin:"+userLogin);
        System.out.println("userPassword:"+userPassword);
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_IMPORT_INITIALIZED, false);
        QBUser user = new QBUser();
        user.setLogin(userLogin);
        user.setPassword(userPassword);
        //showProgress();
        QBLoginCommand.start(this, user);
    }

    private class LoginSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
            relativeLayoutInitializing.setVisibility(View.INVISIBLE);
            relativeLayoutCountinue.setVisibility(View.VISIBLE);
            AnalyticsUtils.pushAnalyticsData(IntializingActivity.this, user, "User Sign In");
            setSetting();
        }
    }

    private class LoginFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception exception = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
            int errorCode = bundle.getInt(QBServiceConsts.EXTRA_ERROR_CODE);
            System.out.println("errorCode:"+errorCode);
            parseExceptionMessage(exception);
        }
    }

    private void setSetting(){

        PrefsHelper helper = new PrefsHelper(this);
        helper.savePref(PrefsHelper.PREF_CONVERSATION_TONE,true);
        helper.savePref(PrefsHelper.PREF_NOTIFICATION,R.raw.notification_v1);
        helper.savePref(PrefsHelper.PREF_NOTIFICATION_NAME,"notification_v1");
        helper.savePref(PrefsHelper.PREF_NOTIFICATION_VIBRATE,true);
    }
}