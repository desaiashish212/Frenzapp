package com.rishi.frendzapp;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBSettings;
import com.rishi.frendzapp.core.lock.LockManager;
import com.rishi.frendzapp.ui.media.MediaPlayerManager;
import com.rishi.frendzapp.utils.ActivityLifecycleHandler;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp.utils.ImageUtils;
import com.rishi.frendzapp_core.utils.PrefsHelper;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static App instance;
    private MediaPlayerManager soundPlayer;
    private RequestQueue mRequestQueue;
    public static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initApplication();
        registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());
    }

    private void initImageLoader(Context context) {
        ImageLoader.getInstance().init(ImageUtils.getImageLoaderConfiguration(context));
    }

    public MediaPlayerManager getMediaPlayer() {
        return soundPlayer;
    }

    private void initApplication() {
        instance = this;
        LockManager.getInstance().enableAppLock(this);
        QBChatService.setDebugEnabled(true);
        initImageLoader(this);
        QBSettings.getInstance().fastConfigInit(Consts.QB_APP_ID, Consts.QB_AUTH_KEY,
                Consts.QB_AUTH_SECRET);
        soundPlayer = new MediaPlayerManager(this);
        new PrefsHelper(this);
    }

    public static synchronized App getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}