package com.rishi.frendzapp.ui.base;

/**
 * Created by AD on 05-May-16.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.rishi.frendzapp.App;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.core.lock.PageListener;
import com.rishi.frendzapp.ui.dialogs.AlertDialog;
import com.rishi.frendzapp.ui.dialogs.ProgressDialog;
import com.rishi.frendzapp.ui.mediacall.CallActivity;
import com.rishi.frendzapp.ui.settings.ChangePasswordActivity;
import com.rishi.frendzapp.ui.splash.SplashActivity;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.qb.commands.QBReloginCommand;
import com.rishi.frendzapp_core.service.ConnectivityListener;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp_core.utils.ErrorUtils;
import com.rishi.frendzapp_core.utils.QBConnectivityManager;

public abstract class ShareBaseActivity extends AppCompatActivity implements ActivityHelper.ServiceConnectionListener, ConnectivityListener {

    public static final int DOUBLE_BACK_DELAY = 2000;

    protected final ProgressDialog progress;
    protected App app;
    protected boolean useDoubleBackPressed;
    protected Fragment currentFragment;
    protected FailAction failAction;
    protected SuccessAction successAction;
    protected ActivityHelper activityHelper;
    protected boolean isNeedShowTostAboutDisconnected;

    private boolean doubleBackToExitPressedOnce;
    private QBService service;


    public ShareBaseActivity() {
        progress = ProgressDialog.newInstance(R.string.dlg_wait_please);
    }

    public FailAction getFailAction() {
        return failAction;
    }

    public QBService getService() {
        return activityHelper.service;
    }

    public synchronized void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    public synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
    }

    public void hideActionBarProgress() {
        activityHelper.hideActionBarProgress();
    }

    public void showActionBarProgress() {
        activityHelper.showActionBarProgress();
    }

    public void addAction(String action, Command command) {
        activityHelper.addAction(action, command);
    }

    public boolean hasAction(String action) {
        return activityHelper.hasAction(action);
    }

    public void removeAction(String action) {
        activityHelper.removeAction(action);
    }

    public void updateBroadcastActionList() {
        activityHelper.updateBroadcastActionList();
    }

    public static PageListener pageListener;

    public static void setListener(PageListener listener) {
        pageListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        app = App.getInstance();
        // actionBar.setDisplayUseLogoEnabled(false);
        failAction = new FailAction();
        successAction = new SuccessAction();
        activityHelper = new ActivityHelper(this, new GlobalListener(), this);
        activityHelper.onCreate();
        addReloginActions();
        if (pageListener != null) {
            pageListener.onActivityCreated(this);
        }

    }

    private void addReloginActions() {
        addAction(QBServiceConsts.RE_LOGIN_IN_CHAT_SUCCESS_ACTION, new ReloginSuccessAction());
        addAction(QBServiceConsts.RE_LOGIN_IN_CHAT_FAIL_ACTION, new ReloginFailAction());
    }

    @Override
    protected void onStart() {
        activityHelper.onStart();
        super.onStart();
        if (pageListener != null) {
            pageListener.onActivityStarted(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isNeedShowTostAboutDisconnected = true;
        activityHelper.onResume();
        addAction(QBServiceConsts.LOGIN_REST_SUCCESS_ACTION, successAction);
        if (pageListener != null) {
            pageListener.onActivityResumed(this);
        }
    }

    @Override
    protected void onPause() {
        activityHelper.onPause();
        super.onPause();
        if (pageListener != null) {
            pageListener.onActivityPaused(this);
        }
    }

    @Override
    protected void onStop() {
        isNeedShowTostAboutDisconnected = false;
        activityHelper.onStop();
        super.onStop();
        if (pageListener != null) {
            pageListener.onActivityStopped(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAction(QBServiceConsts.RE_LOGIN_IN_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.RE_LOGIN_IN_CHAT_FAIL_ACTION);
        if (pageListener != null) {
            pageListener.onActivityDestroyed(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (pageListener != null) {
            pageListener.onActivitySaveInstanceState(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce || !useDoubleBackPressed) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        DialogUtils.show(this, getString(R.string.dlg_click_back_again));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, DOUBLE_BACK_DELAY);
    }

    @Override
    public void onConnectedToService(QBService service) {
        QBConnectivityManager.getInstance(this).addConnectivityListener(this);
    }

    protected void navigateToParent() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        if (intent == null) {
            finish();
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T _findViewById(int viewId) {
        return (T) findViewById(viewId);
    }

    protected void setCurrentFragment(Fragment fragment) {
        currentFragment = fragment;
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = buildTransaction();
        transaction.replace(R.id.container, fragment, null);
        transaction.commit();
    }

    private FragmentTransaction buildTransaction() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        return transaction;
    }

    protected void onFailAction(String action) {
    }

    private boolean needShowReceivedNotification() {
        boolean isSplashActivity = activityHelper.getContext() instanceof SplashActivity;
        boolean isCallActivity = activityHelper.getContext() instanceof CallActivity;
        boolean isChangePasswordActivity = activityHelper.getContext() instanceof ChangePasswordActivity;
        return !isSplashActivity && !isCallActivity && !isChangePasswordActivity;
    }

    protected void onSuccessAction(String action) {

    }

    public class FailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception e = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
            ErrorUtils.showError(ShareBaseActivity.this, e);
            hideProgress();
            hideActionBarProgress();
            onFailAction(bundle.getString(QBServiceConsts.COMMAND_ACTION));
        }
    }

    public class SuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            onSuccessAction(bundle.getString(QBServiceConsts.COMMAND_ACTION));
        }
    }

    private class GlobalListener implements ActivityHelper.GlobalActionsListener {

        @Override
        public void onReceiveChatMessageAction(Bundle extras) {
            if (needShowReceivedNotification()) {
                activityHelper.onReceivedChatMessageNotification(extras);
            }
        }

        @Override
        public void onReceiveForceReloginAction(Bundle extras) {
            activityHelper.forceRelogin();
        }

        @Override
        public void onReceiveRefreshSessionAction(Bundle extras) {
            DialogUtils.show(ShareBaseActivity.this, getString(R.string.dlg_refresh_session));
            activityHelper.refreshSession();
        }

        @Override
        public void onReceiveContactRequestAction(Bundle extras) {
            if (needShowReceivedNotification()) {
                activityHelper.onReceivedContactRequestNotification(extras);
            }
        }
    }

    public class ReloginSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Toast.makeText(ShareBaseActivity.this, getString(R.string.relgn_success), Toast.LENGTH_LONG).show();
        }
    }

    public class ReloginFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Toast.makeText(ShareBaseActivity.this, getString(R.string.relgn_fail), Toast.LENGTH_LONG).show();
            AlertDialog dialog = AlertDialog.newInstance(getString(R.string.relgn_fail));
            dialog.setPositiveButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getService().forceRelogin();
                }
            });
            dialog.setNegativeButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ShareBaseActivity.this, getString(R.string.dont_forget_relogin), Toast.LENGTH_LONG).show();
                }
            });
            dialog.show(getFragmentManager(), null);
        }
    }

    public boolean isConnectionEnabled() {
        return QBConnectivityManager.isConnectionExists();
    }

    @Override
    public void onConnectionChange(boolean isConnected) {
        if (isConnected) {
            QBReloginCommand.start(this);
        } else {
            showToastAboutDisconnectedIfNeed();
        }
    }

    private void showToastAboutDisconnectedIfNeed() {
        if (isNeedShowTostAboutDisconnected){
            Toast.makeText(this, this.getString(com.rishi.frendzapp_core.R.string.connection_lost), Toast.LENGTH_LONG).show();
        }
    }
}