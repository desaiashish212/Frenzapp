package com.rishi.frendzapp.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.quickblox.auth.QBAuth;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.server.BaseService;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.agreeandactivate.AgreeAndActivateActivity;
import com.rishi.frendzapp.ui.authorization.helper.PrefManager;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp.ui.main.MainActivity;
import com.rishi.frendzapp.utils.AnalyticsUtils;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.LoginType;
import com.rishi.frendzapp_core.qb.commands.QBLoginCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.ErrorUtils;
import com.rishi.frendzapp_core.utils.PrefsHelper;
import com.quickblox.users.model.QBUser;
import io.fabric.sdk.android.Fabric;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crashlytics.start(this);
        Fabric.with(this, new Crashlytics());
        addActions();

        PrefManager pref = new PrefManager(getApplicationContext());
        HashMap<String, String> profile = pref.getUserDetails();
        String user_id = profile.get("mobile");
        String password = profile.get("password");
        boolean isRememberMe = pref.getIsremember();

        //String date = PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_EXPIRE_DATE);

           // System.out.println("ExpireDate:"+baseService.getTokenExpirationDate());


        if (isRememberMe) {
            checkStartExistSession(user_id, password);
        } else {
            startLanding();
        }

        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onStart() {
        super.onStart();
        //     facebookHelper.onActivityStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedInToChat()) {
            startMainActivity();
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //    facebookHelper.onActivityStop();
    }

    @Override
    protected void onFailAction(String action) {
        super.onFailAction(action);
        startLanding();
    }

    private boolean isLoggedInToChat() {
        return QBChatService.isInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    private void checkStartExistSession(String userEmail, String userPassword) {
        boolean isEmailEntered = !TextUtils.isEmpty(userEmail);
        boolean isPasswordEntered = !TextUtils.isEmpty(userPassword);
        if ((isEmailEntered && isPasswordEntered)) {
            runExistSession(userEmail, userPassword);
        } else {
            startLanding();
        }
    }

    private void addActions() {
        addAction(QBServiceConsts.LOGIN_SUCCESS_ACTION, new LoginSuccessAction());
        addAction(QBServiceConsts.LOGIN_AND_JOIN_CHATS_FAIL_ACTION, failAction);
        addAction(QBServiceConsts.LOGIN_FAIL_ACTION, failAction);
    }

    /*   public boolean isLoggedViaFB() {
           return facebookHelper.isSessionOpened() && LoginType.FACEBOOK.equals(getCurrentLoginType());
       }
   */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // facebookHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  facebookHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void startLanding() {
        AgreeAndActivateActivity.start(SplashActivity.this);
        finish();
    }

    private void runExistSession(String userEmail, String userPassword) {
        //check is token valid for about 1 minute
        if (AppSession.isSessionExistOrNotExpired(TimeUnit.MINUTES.toMillis(ConstsCore.TOKEN_VALID_TIME_IN_MINUTES))) {
            startMainActivity();
            finish();
        } else {
            System.out.println("In doAutoLogin");
            doAutoLogin(userEmail, userPassword);
        }
    }

    private void doAutoLogin(String userEmail, String userPassword) {
        if (LoginType.LOGIN.equals(getCurrentLoginType())) {
            login(userEmail, userPassword);
        }
    }

    private void login(String userEmail, String userPassword) {
        QBUser user = new QBUser();
        user.setLogin(userEmail);
        user.setPassword(userPassword);
        QBLoginCommand.start(this, user);
    }

    private LoginType getCurrentLoginType() {
        return AppSession.getSession().getLoginType();
    }

    private void startMainActivity() {
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.PREF_IMPORT_INITIALIZED, true);
        MainActivity.start(SplashActivity.this);
    }

    /*    private class FacebookSessionStatusCallback implements Session.StatusCallback {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened() && LoginType.FACEBOOK.equals(getCurrentLoginType())) {
                    QBLoginRestWithSocialCommand.start(SplashActivity.this, QBProvider.FACEBOOK,
                            session.getAccessToken(), null);
                }
            }
        }
    */
    private class LoginSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);

            startMainActivity();

            AnalyticsUtils.pushAnalyticsData(SplashActivity.this, user, "User Sign In");

            finish();
        }
    }
}