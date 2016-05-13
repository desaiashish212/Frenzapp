package com.rishi.frendzapp.ui.share;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rishi.frendzapp.App;
import com.rishi.frendzapp.ui.base.ActivityHelper;
import com.rishi.frendzapp.ui.base.ShareBaseActivity;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.utils.QBConnectivityManager;

/**
 * Created by AD on 04-May-16.
 */
public abstract class ShareBaseFragment extends Fragment implements ActivityHelper.ServiceConnectionListener{

    protected static final String ARG_TITLE = "title";

    protected App app;
    protected ShareBaseActivity baseActivity;
    protected ShareBaseActivity.FailAction failAction;
    protected String title;

    public String getTitle() {
        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity =(ShareBaseActivity) getActivity();
        failAction = baseActivity.getFailAction();
        app = App.getInstance();
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
       // getActivity().getActionBar().setTitle(title);
    }

    public ShareBaseActivity getBaseActivity() {
        return (ShareBaseActivity) getActivity();
    }

    protected boolean isExistActivity() {
        return ((!isDetached()) && (getBaseActivity() != null));
    }

    @Override
    public void onConnectedToService(QBService service) {
        QBConnectivityManager.getInstance(baseActivity).addConnectivityListener(baseActivity);
    }

}