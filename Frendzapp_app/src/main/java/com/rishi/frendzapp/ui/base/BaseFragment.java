package com.rishi.frendzapp.ui.base;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rishi.frendzapp.App;
import com.rishi.frendzapp.core.lock.LockManager;
import com.rishi.frendzapp.ui.main.MainActivity;

public abstract class BaseFragment extends Fragment {

    protected static final String ARG_TITLE = "title";

    protected App app;
    protected BaseActivity baseActivity;
    protected BaseActivity.FailAction failAction;
    protected String title;
    protected ActionBar actionBar;

    public String getTitle() {
        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
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
        getActivity().getActionBar().setTitle(title);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected boolean isExistActivity() {
        return ((!isDetached()) && (getBaseActivity() != null));
    }
}