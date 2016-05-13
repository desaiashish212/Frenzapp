package com.rishi.frendzapp.ui.base;

import android.app.Activity;
import android.util.Log;

import com.rishi.frendzapp_core.core.ui.LoaderHelper;
import com.rishi.frendzapp_core.core.ui.LoaderManager;
import com.rishi.frendzapp_core.core.ui.OnLoadFinishedListener;
import com.rishi.frendzapp_core.utils.ErrorUtils;

public class QMLoaderHelper<T> extends LoaderHelper<T> {

    private static final String TAG = QMLoaderHelper.class.getSimpleName();

    public QMLoaderHelper(Activity activity, OnLoadFinishedListener<T> loadFinishedListener,
            LoaderManager<T> loaderManager) {
        super(activity, loadFinishedListener, loaderManager);
    }

    @Override
    public void onLoaderException(int i, Exception e) {
        Log.e(TAG, "Unknown error during loading", e);
        ErrorUtils.showError(activity, e);
    }
}